package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.streams

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.CountDownTimer
import android.os.Handler
import android.text.format.DateUtils
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.ContentDetailsResponse
import com.radyopilipinomediagroup.radyo_now.model.GeneralResultModel
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.model.stations.StationContentsResultModel
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.comments.CommentFragment
import com.radyopilipinomediagroup.radyo_now.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AudioStreamPresenter(var view: AudioStreamFragment) : AbstractPresenter<AudioStreamFragment>(view) {

    var timer: CountDownTimer? = null
    var notifBuilder: NotificationBuilder? = null
    var notifService : NotificationService? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private var runnable: Runnable? = null
    private var durationHandler: Handler = Handler()
    private var fragmentManager = view.activity().supportFragmentManager
    private var audioContentList = mutableListOf<StationContentsResultModel.Data>()
    private var allContents = mutableListOf<StationContentsResultModel.Data>()
    private var mainContents = mutableListOf<StationContentsResultModel.Data>()
    private var audioPos: Int? = 0
    private var streamURL = ""
    private var isPaused = false
    private var isFinished = false
    private var getContentDetails: ContentDetailsResponse? = null
    private val stationId: String? = view.arguments!!.getString("stationId")
    private var contentId: String? = view.arguments!!.getString("contentId")
    private val stationName: String? = view.arguments!!.getString("stationName")
    private var programID: String? = ""
    private var serviceIntent: Intent? = null
    private var stationBitmap: Bitmap? = null
    private var adapter = AudioStreamAdapter(
        view.context(),
        allContents,
        stationId?.toInt()
    )
    private var llm = LinearLayoutManager(view.context())
    private var bitmapPlaceHolder = BitmapFactory.decodeResource(view.activity?.resources, R.drawable.ic_audio_stream_holder)
    init {
        initHandler()
        setToolbarSettings()
//        displayFeaturedStations()
        getAds()
        setupRecycler()
        getContentDetails()
    }

    private fun initHandler(){
        notifService = NotificationService()
        firebaseAnalytics = Firebase.analytics
    }

    private fun setTimerAnalytics(data: ContentDetailsResponse?) {
        timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                Services.setDataAnalytics(firebaseAnalytics,
                    data?.getData()?.name,
                    data?.getData()?.program?.name,
                    data?.getData()?.id.toString(),
                    "Programs",
                    "select_content")
            }
        }.start()
    }

    private fun setupRecycler() {
        view.getRecycler().adapter = adapter
        view.getRecycler().layoutManager = llm
    }

    fun openCommentPage(){
        Services.changeFragment(fragmentManager, CommentFragment(),
            "CommentFragment", "contentId", stationId)
    }

    private fun getStationsAudio() {
        audioContentList.clear()
        allContents.clear()
        mainContents.clear()
        getRepositories?.getProgramContents(
            programID!!,
            getSessionManager?.getToken()!!,
            object :
                RetrofitService.ResultHandler<StationContentsResultModel> {
                override fun onSuccess(data: StationContentsResultModel?) {
                    mainContents.addAll(data?.data!!)
                    data.data.forEach {
                        if (it.id.toString() != contentId) allContents.add(it)
                        if (it.format == "audio") audioContentList.add(it)
                    }

                    println("getStationsAudio: ${Gson().toJson(audioContentList)}")

                    adapter.notifyDataSetChanged()
                    if(allContents.size == 0)  view.getTxtNoPrograms().visibility = android.view.View.VISIBLE

                    mapAudioPos()
                }

                override fun onError(error: StationContentsResultModel?) {
                    Log.d("getStationsAudioError", error?.message!!)
                    view.getTxtNoPrograms().visibility = android.view.View.VISIBLE
                }

                override fun onFailed(message: String) {
                    Log.d("getStationsAudioContent", message)
                    view.getTxtNoPrograms().visibility = android.view.View.VISIBLE
                }
            })
    }

    fun mapAudioPos() {
        try {
            if (contentId.toString() != "null")
                if (audioContentList.size > 0)
                    for (i in 0 until audioContentList.size)
                        if (contentId?.toInt() == audioContentList[i].id) audioPos = i
        } catch (e: Exception){}
    }

    private fun getContentIDInStation(): Int = audioContentList[audioPos!!].id!!

    fun getBroadcastReceiverData(audioControls: String) {
        when (audioControls) {
            //Reading the live data coming from event bus.
            "Play" -> startAudioPlayer()
            "Pause" -> pauseAudioPlayer()
            "Previous" -> prevAudio()
            "Next" -> nextAudio()
            "Close" -> {
                //Register the Event Bus to start the service
                EventBus.getDefault().unregister(view.context)
                stopAudioPlayer()
            }
        }
    }

    private fun startAudioPlayer(){
        isPaused = true
        view.changePlayerIcon(R.drawable.ic_pause_icon)
        notifService?.mediaPlayer?.startAudioPlayer()
    }

    private fun pauseAudioPlayer(){
        isPaused = false
        view.changePlayerIcon(R.drawable.ic_play_logo)
        notifService?.mediaPlayer?.pauseAudioPlayer()
    }

    private fun stopAudioPlayer(){
        isPaused = false
        notifService?.mediaPlayer?.stopMediaPlayer()
        view.changePlayerIcon(R.drawable.ic_play_logo)
    }

    private fun createAudioNotificationPlayer() = runBlocking {
        try {
            serviceIntent = Intent(view.context, NotificationService::class.java)
            serviceIntent!!.action = STARTFOREGROUND_ACTION
            view.context!!.startService(serviceIntent)
            if (getContentDetails!!.getData()!!.thumbnail.toString() == "null") {
                notifBuilder!!.showMusicPlayerNotification(
                    view.context(),
                    isPaused,
                    bitmapPlaceHolder,
                    getContentDetails?.getData()?.name.toString(),
                    getContentDetails?.getData()?.broadcastDate.toString(),
                    getContentDetails?.getData()?.ageRestriction.toString()
                )
            } else {
                if (null == notifBuilder) notifBuilder = NotificationBuilder()
                val url = URL(getContentDetails!!.getData()!!.thumbnail.toString())
                withContext(Dispatchers.IO) {
                    try {
                        val input = url.openStream()
                        BitmapFactory.decodeStream(input)
                    } catch (e: IOException) {
                        null
                    }
                }?.let { bitmap ->
                    stationBitmap = bitmap
                    notifBuilder!!.showMusicPlayerNotification(
                        view.context(),
                        isPaused,
                        bitmap,
                        getContentDetails?.getData()?.name.toString(),
                        getContentDetails?.getData()?.broadcastDate.toString(),
                        getContentDetails?.getData()?.ageRestriction.toString()
                    )
                }
            }
        } catch (e: Exception) { }
    }

    @SuppressLint("SetTextI18n")
    private fun setToolbarSettings() {
        getToolbarDrawerIcon!!.visibility = android.view.View.GONE
        getToolbarLogo!!.visibility = android.view.View.VISIBLE
        getToolbarText!!.visibility = android.view.View.GONE
        getToolbarBack!!.visibility = android.view.View.VISIBLE

        getToolbarBack!!.setOnClickListener {
            (it.context as DashboardActivity).getBackStacked()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    var seekBarListener = (android.view.View.OnTouchListener { p0, p1 ->
        changeCurrentProgress()
        false
    })

    @SuppressLint("ClickableViewAccessibility")
    var seekChangeListener = (object : SeekBar.OnSeekBarChangeListener {
        private var mProgressAtStartTracking = 0
        private val SENSITIVITY = 0

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            mProgressAtStartTracking = view.getSeekBar().progress
        }
        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {
            if(Math.abs(mProgressAtStartTracking - view.getSeekBar().progress) <= SENSITIVITY)
                changeCurrentProgress()
        }
    })

    private fun changeCurrentProgress(){
        notifService?.mediaPlayer?.seekToPosition(view.getSeekBar().progress)
        view.getTextCurrentDuration().text =
            Services.getTotalTextDuration(notifService?.mediaPlayer!!.getCurrentPosition())
        view.getSeekBar().progress = view.getSeekBar().progress
        changeSeekBarDuration()
        checkIsFinished()
    }

    private fun getContentDetails() {
        try {
            getRepositories?.getContentDetails(
                getSessionManager?.getToken()!!,
                contentId.toString(),
                object :
                    RetrofitService.ResultHandler<ContentDetailsResponse> {
                    override fun onSuccess(data: ContentDetailsResponse?) {
                        try {
                            setTimerAnalytics(data)
                            getContentDetails = data
                            getToolbarText?.text = data!!.getData()!!.name
                            streamURL = data.getData()!!.contentUrl.toString()

                            if(getContentDetails?.getData()?.isFavorite!!)
                                view.getImageHeart().
                                setImageDrawable(
                                    ContextCompat.getDrawable(view.context!!,R.drawable.ic_favorite_icon)
                                )
                            else
                                view.getImageHeart().
                                setImageDrawable(
                                    ContextCompat.getDrawable(view.context!!,R.drawable.ic_heartline_20dp)
                                )
                            view.setStreamDetails(
                                data.getData()!!.name.toString(),
                                data.getData()!!.broadcastDate.toString(),
                                data.getData()!!.thumbnail.toString(),
                                data.getData()!!.contentUrl.toString(),
                                data.getData()!!.description.toString())
                            programID = data.getData()?.program?.id.toString()

                            //Function for auto play the audio.
                            initMediaType(streamURL)
//                            displayFeaturedStations()
                            getStationsAudio()
                        } catch (e: Exception) {
                            Log.d("ExceptionError: ", e.message.toString())
                        }
                    }

                    override fun onError(error: ContentDetailsResponse?) {
                        Log.d("ErrorContent", error?.getMessage()!!)
                        //Reinitialize the Function for auto play the audio if the request above is failed.
                        initMediaType(streamURL)
                    }

                    override fun onFailed(message: String) {
                        Log.d("FailedContent", message)
                    }
                })
        } catch (e: Exception) {
            e.message
        }
    }

    private fun displayFeaturedStations(programId: String?){
        getRepositories?.stationContents(
            programId!!.toInt(),
            4,
            "audio",
            getSessionManager?.getToken()!!,
            object :
                RetrofitService.ResultHandler<StationContentsResultModel> {
                override fun onSuccess(response: StationContentsResultModel?) {
                    view.getRecycler().adapter = AudioStreamAdapter(
                        view.context(),
                        response!!.data,
                        stationId?.toInt()
                    )
                    view.getRecycler().layoutManager = LinearLayoutManager(view.context(), LinearLayoutManager.HORIZONTAL, false)
                }

                override fun onError(error: StationContentsResultModel?) {
                    Log.d("ErrorContent", error?.message!!)
                    view.getTxtNoPrograms().visibility = android.view.View.VISIBLE
                }

                override fun onFailed(message: String) {
                    Log.d("FailedContent", message)
                }
            })
    }

    fun initializeMediaPlayer() = try {
        when(isFinished) {
            true -> resetMediaPlayer()
            else -> {
                changeImagePlayAppearance()
                notifService?.mediaPlayer?.isReady = true
                notifService?.mediaPlayer?.callback = object : AudioPlayer.MediaPlayerCallBack {
                    override fun onPrepared() {
                        when(notifService?.mediaPlayer?.isReady) {
                            true -> {
                                changeSeekBarDuration()
                                notifService?.mediaPlayer?.stopMediaPlayer()
                                notifService?.mediaPlayer?.startAudioPlayer()
                            }
                        }
                        setMediaProgressDetails()
                    }
                    override fun onBuffered(progress: Int) = view.setAudioSecondaryProgress(progress)
                    override fun onCompleted() = onFinishNotification()
                }
            }
        }
    } catch (e: Exception) {
        log(e.message.toString())
    }

    private fun setMediaProgressDetails(){
        val totalTextDuration =
            Services.getTotalTextDuration(notifService?.mediaPlayer!!.getDuration()) // duration in time in millis
        val totalDuration = Services.getTotalDuration(notifService?.mediaPlayer!!.getDuration())
        val duration: Int =
            notifService?.mediaPlayer!!.getCurrentPosition() / 1000 // duration in time in millis
        val currentDuration =
            DateUtils.formatElapsedTime((duration).toLong()).toString()
        view.setMediaPlayerDetails(currentDuration, totalTextDuration, totalDuration)
        view.getSeekBar().progress = 0
        view.getSeekBar().max = notifService?.mediaPlayer!!.getDuration()
    }

    private fun setProgressTextDetails(){
        val duration: Int =
            notifService?.mediaPlayer!!.getCurrentPosition() / 1000 // duration in time in millis
        val currentDuration =
            DateUtils.formatElapsedTime((duration).toLong()).toString()
        view.getTextCurrentDuration().text = currentDuration
    }

    //If the audio has been finished, changed the appearance of the notification.
    private fun onFinishNotification() {
        try {
            //If the current station has no station logo, get the
            //default place holder and set it on notification.
            if (getContentDetails!!.getData()!!.thumbnail.toString() == "null") stationBitmap =
                bitmapPlaceHolder
            notifBuilder!!.showMusicPlayerNotification(
                view.context(),
                true,
                stationBitmap!!,
                getContentDetails?.getData()?.name.toString(),
                getContentDetails?.getData()?.broadcastDate.toString(),
                getContentDetails?.getData()?.ageRestriction.toString()
            )
            view.changePlayerIcon(R.drawable.ic_play_logo)
            isFinished = true
        } catch (e: Exception) {}
    }

    private fun resetMediaPlayer(){
        view.getSeekBar().progress = 0
        view.changePlayerIcon(R.drawable.ic_pause_icon)
        notifService?.mediaPlayer?.seekToPosition(0)
        notifService?.mediaPlayer?.startAudioPlayer()
        changeSeekBarDuration()
        isFinished = false
    }

    private fun changeImagePlayAppearance() {
        if (notifService?.mediaPlayer?.audioPlayer?.isPlaying == true) {
            durationHandler.removeCallbacks(runnable!!)
            view.changePlayerIcon(R.drawable.ic_play_logo)
            notifService?.mediaPlayer?.pauseAudioPlayer()
            isPaused = true
            changeSeekBarDuration()
            view.activity!!.runOnUiThread { createAudioNotificationPlayer() }
        } else try {
            if (isPaused) {
                notifService?.mediaPlayer?.startAudioPlayer()
                isPaused = false
            }
            changeSeekBarDuration()
            view.changePlayerIcon(R.drawable.ic_pause_icon)
            view.activity!!.runOnUiThread { createAudioNotificationPlayer() }
        } catch (e: IllegalArgumentException) {
        } catch (e: IllegalStateException) {
        } catch (e: IOException) {
        }
    }

    private fun initMediaType(streamURL: String) {
        if (streamURL != "") {
            notifService?.mediaPlayer?.initMediaType(streamURL)
            initializeMediaPlayer()
        }
    }

    fun backwardForward(seconds: Int, operator: String) {
        var posForward: Int? = 0
        when (operator) {
            "+" -> {
                posForward = if (notifService?.mediaPlayer!!.getDuration() - notifService?.mediaPlayer!!.getCurrentPosition() >= 30000) {
                    notifService?.mediaPlayer!!.getCurrentPosition() + seconds
                } else notifService?.mediaPlayer!!.getDuration() - 3000
            }
            "-" -> {
                posForward = if (notifService?.mediaPlayer!!.getCurrentPosition() >= 30000) {
                    notifService?.mediaPlayer!!.getCurrentPosition() - seconds
                } else 0
            }
        }
        view.getSeekBar().progress = posForward!!
        notifService?.mediaPlayer!!.seekToPosition(posForward)
        setProgressTextDetails()
        checkIsFinished()
    }

    private fun checkIsFinished(){
        //Checking if the player has been finished and if the seekbar and backward/forward has been clicked.
        //Then start again the audio player to play it and set the boolean(isFinished) to false.
        if (isFinished) {
            isFinished = false
            notifService?.mediaPlayer?.startAudioPlayer()
            view.changePlayerIcon(R.drawable.ic_pause_icon)
        }
    }

    private fun changeSeekBarDuration() {
        runnable = Runnable {
            if (notifService?.mediaPlayer?.audioPlayer?.isPlaying == true) {
                try {
                    changeSeekBarDuration()
                    view.activity?.runOnUiThread {
                        val mCurrentPosition: Int = notifService?.mediaPlayer!!.getCurrentPosition() / 1000
                        view.getTextCurrentDuration().text =
                            DateUtils.formatElapsedTime((mCurrentPosition).toLong()).toString()
                        view.getSeekBar().progress = notifService?.mediaPlayer!!.getCurrentPosition()
                    }
                } catch (e: Exception) {
                }
            }
        }
        durationHandler.postDelayed(runnable!!, 1000)
    }

    fun showPopUpMenu() {

        val wrapper = ContextThemeWrapper(
            view.context,
            R.style.popupMenu
        )
        val popup = PopupMenu(wrapper, view.getImgOption())
        popup.menuInflater?.inflate(R.menu.popup_menu_stream, popup.menu)
        val likeMenu = popup.menu.findItem(R.id.like)
        if(getContentDetails?.getData()?.isFavorite!!) {
            likeMenu.title = "Unlike"
            likeMenu.icon = ContextCompat.getDrawable(view.context!!, R.drawable.ic_favorite_icon)
        }
        else {
            likeMenu.title = "Like"
            likeMenu.icon =
                ContextCompat.getDrawable(view.context!!, R.drawable.ic_favorite_no_outline_icon_gray)
        }
        setForceShowIcon(popup)
        popup.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.addToPlaylist -> {
                    try {
                        (view.context as DashboardActivity).passData(stationId!!.toInt())
                    } catch (e: java.lang.Exception) {
                    }
                    return@setOnMenuItemClickListener true
                }
                R.id.shareContent -> {
                    shareContent()
                    return@setOnMenuItemClickListener true
                }
                R.id.like -> {
                    if(checkRestriction(Constants.ADD_FAVORITES) == false)  return@setOnMenuItemClickListener false
                    addDeleteFavorites()
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
        popup.show()
    }

    private fun shareContent(){
        val buo = BranchObject.buo
        buo.title = getContentDetails?.getData()?.name
        buo.setContentDescription(getContentDetails?.getData()?.description)
        BranchObject.setLinkProperties(getContentDetails?.getData()?.name!!,getContentDetails?.getData()?.id.toString(),getContentDetails?.getData()?.format!!)

        buo.generateShortUrl(view.context(), BranchObject.linkProperties!!
        ) { url, error ->
            println("shareContent: ${error == null}")
            val newUrl = if(error == null) url
            else "https://radyopilipino.app.link/${getContentDetails?.getData()?.id}"
            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.putExtra(Intent.EXTRA_TEXT, newUrl)
            share.type = "text/plain"
            view.activity().startActivity(Intent.createChooser(share, "Share via..."))
        }
    }

    private fun setForceShowIcon(popupMenu: PopupMenu) {
        try {
            val fields: Array<Field> = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.getName()) {
                    field.setAccessible(true)
                    val menuPopupHelper: Any = field.get(popupMenu)
                    val classPopupHelper = Class.forName(
                        menuPopupHelper
                            .javaClass.name
                    )
                    val setForceIcons: Method = classPopupHelper.getMethod(
                        "setForceShowIcon", Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun nextAudio() {
        audioPos = if (audioContentList.size > 1 && audioPos!! < audioContentList.lastIndex) {
            audioPos?.plus(1)
        } else 0
        updateAudioPlayer()
    }

    fun prevAudio() {
        audioPos = if (audioContentList.size > 1 && audioPos!! > 0) {
            audioPos?.minus(1)
        } else audioContentList.lastIndex
        updateAudioPlayer()
    }

    private fun updateAudioPlayer() {
        contentId = getContentIDInStation().toString()
        val data = audioContentList[audioPos!!]
        streamURL = data.contentUrl.toString()
        // Function to  display Stream Data
        view.setStreamDetails(
            data.name.toString(),
            data.broadcast_date.toString(),
            data.thumbnail.toString(),
            data.contentUrl.toString(),
            data.description.toString())
        // Function for auto play the audio.
        initMediaType(streamURL)
        allContents.clear()
        mainContents.forEach {
            if (it.id.toString() != contentId) allContents.add(it)
        }
        adapter.notifyDataSetChanged()
//        getContentDetails()
    }

    private fun getAds() {
        val stationId: String? = view.arguments!!.getString("stationId")
        getRepositories?.getAds(getSessionManager?.getToken()!!,
            "content", stationId!!.toInt(),
            object : RetrofitService.ResultHandler<AdsModel> {
                override fun onSuccess(data: AdsModel?) {
                    println("getAds: ${Gson().toJson(data)}")
                    data?.data?.forEach { ads ->
                        if (ads.active!!) {
                            if (ads.section == "content" && ads.type == "banner") {
                                var strDateFrom = Services.convertDate(ads.durationFrom, "" +
                                        "yyyy-MM-dd", "yyyy/MM/dd")
                                var strDateEnd = Services.convertDate(ads.durationTo, "" +
                                        "yyyy-MM-dd", "yyyy/MM/dd")

                                val dateFormat = SimpleDateFormat("yyyy/MM/dd")
                                try {
                                    val dateFrom: Date? = dateFormat.parse(strDateFrom)
                                    val dateEnd: Date? = dateFormat.parse(strDateEnd)
                                    if (Services.dateBetween(Services.getCurrentDate(), dateFrom, dateEnd)) {
                                        view.onBannerReady(ads)
                                    }
                                } catch (e: ParseException) { }
                            }
                        }
                    }
                }

                override fun onError(error: AdsModel?) {
                    println("getAdsError: ${Gson().toJson(error)}")
                    view.getBannerAds().visibility = android.view.View.GONE
                }

                override fun onFailed(message: String) {
                    println("getAdsFailed: $message")
                    view.getBannerAds().visibility = android.view.View.GONE
                }
            })
    }

    fun addDeleteFavorites() {
        val userFavorite = getContentDetails?.getData()?.isFavorite!!
        val id = getContentDetails?.getData()?.id.toString()
        if(userFavorite){
            deleteFavorite(id, true)
        } else {
            addToFavorites(id, true)
        }
    }

    fun deleteFavorite(contentId: String, isStream: Boolean){
        getRepositories?.deleteFavorite(
            contentId,
            getSessionManager?.getToken()!!,
            object: RetrofitService.ResultHandler<GeneralResultModel>{
                override fun onSuccess(data: GeneralResultModel?) {
                    if(isStream) {
                        changeImageAnimation(R.drawable.ic_heartline_20dp)
                        getContentDetails?.getData()?.isFavorite = false
                    }else getStationsAudio()
                }
                override fun onError(error: GeneralResultModel?) {
                    view.context?.toast("${error?.message}")
                }
                override fun onFailed(message: String) {
                    view.context?.toast("Failed to load. Please try again!")
                }
            }
        )
    }

    private fun changeImageAnimation(icon: Int) {
        val animIn = AnimationUtils.loadAnimation(view.context, android.R.anim.fade_in)
        val animOut = AnimationUtils.loadAnimation(view.context, android.R.anim.fade_out)
        animOut.duration = 200
        animIn.duration = 200
        animOut.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                view.getImageHeart().setImageDrawable(ContextCompat.getDrawable(view.context!!, icon))
                view.getImageHeart().startAnimation(animIn)
            }
        })
        view.getImageHeart().startAnimation(animOut)
    }

    fun addToFavorites(contentId: String, isStream: Boolean) {
        getRepositories?.addToFavorites(
            contentId,
            getSessionManager?.getToken()!!,
            object: RetrofitService.ResultHandler<GeneralResultModel>{
                override fun onSuccess(data: GeneralResultModel?) {
                    if(isStream) {
                        changeImageAnimation(R.drawable.ic_favorite_icon)
                        getContentDetails?.getData()?.isFavorite = true
                    } else getStationsAudio()
                }
                override fun onError(error: GeneralResultModel?) {
                    view.context?.toast("${error?.message}")
                }
                override fun onFailed(message: String) {
                    view.context?.toast("Failed to load. Please try again!")
                }
            }
        )
    }

    fun processDataAnalytics(data: AdsModel.Data){
        Services.openBrowser(view.context!!, data.assets[0].link.toString())
        Services.setDataAnalytics(
            firebaseAnalytics,
            data.title,
            data.location,
            data.id.toString(),
            "Location",
            "select_ad")
    }

    fun checkRestriction(buttonId: Int): Boolean? {
        if (getSessionManager?.isGuest() == true){
            view.context().guestWarningDialog()
            return false
        }
        when(buttonId){
            R.id.txtViewComments -> openCommentPage()
            R.id.imgHeart -> addDeleteFavorites()
        }
        return true
    }

    interface View : AbstractView {
        fun getImageHeart(): ImageView
        fun setMediaPlayerDetails(
            currentDuration: String,
            totalTextDuration: String,
            totalDuration: Int
        )
        fun getBannerAds() : ImageView
        fun getRecycler() : RecyclerView
        fun setAudioSecondaryProgress(secondaryProgress: Int)
        fun changePlayerIcon(drawableIcon: Int)
        fun getImgOption(): ImageView
        fun getTxtNoPrograms() : TextView
        fun getTextCurrentDuration(): TextView
        fun getSeekBar(): SeekBar
        fun setStreamDetails(
            stationName: String,
            stationDate: String,
            thumbURL: String,
            streamURLResult: String,
            description: String
        )
    }
}