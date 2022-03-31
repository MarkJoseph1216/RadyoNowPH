package com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.featuredstreaming

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.CountDownTimer
import android.os.Handler
import android.text.format.DateUtils
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.halilibo.bvpkotlin.BetterVideoPlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.ContentDetailsResponse
import com.radyopilipinomediagroup.radyo_now.model.GeneralResultModel
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.model.stations.StationContentsResultModel
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.comments.CommentFragment
import com.radyopilipinomediagroup.radyo_now.utils.*
import io.supercharge.shimmerlayout.ShimmerLayout
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class FeaturedPresenter(var view: FeaturedStreamFragment) : AbstractPresenter<FeaturedStreamFragment>(
    view
) {

    var getContentDetails : ContentDetailsResponse? = null
    var timer: CountDownTimer? = null
    var notifService: NotificationService? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private var fragmentManager = view.activity().supportFragmentManager
    private var mediaPlayer = MediaPlayer()
    private var runnable: Runnable? = null
    private var durationHandler: Handler = Handler()
    private var streamURL = ""
    private var stationType = ""
    private var isPaused = false
    var programList = mutableListOf<StationContentsResultModel.Data>()
    val stationId: String? = view.arguments!!.getString("stationId")
    val contentId: String? = view.arguments!!.getString("contentId")
    val stationName: String? = view.arguments!!.getString("stationName")
    val stationTypes: String? = view.arguments!!.getString("stationType")
    private var programID: String? = ""
    var programsAdapter = FeaturedAdapter(
        view.context!!,
        programList,
        stationId.toString(),
        stationName.toString()
    )
    var llm = LinearLayoutManager(view.context!!)

    init {
        initHandler()
    }

    private fun initHandler(){
        notifService = NotificationService()
        firebaseAnalytics = Firebase.analytics
    }

    private fun setTimerAnalytics(data: ContentDetailsResponse?) {
        timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                Services.setDataAnalytics(
                    firebaseAnalytics,
                    data?.getData()?.name,
                    data?.getData()?.program?.name,
                    data?.getData()?.id.toString(),
                    "Programs",
                    "select_content"
                )
            }
        }.start()
    }

    fun setProgramsRecycler(){
        view.getRecycler().adapter = programsAdapter
        view.getRecycler().layoutManager = llm
    }

    fun startShimmerLayout() {
        getView!!.getShimmerLayout().visibility = android.view.View.VISIBLE
        getView!!.getShimmerLayout().startShimmerAnimation()
        getView!!.getRecycler().visibility = android.view.View.GONE
    }

    @SuppressLint("WrongConstant")
    fun stopShimmerLayout() {
        getView!!.getShimmerLayout().stopShimmerAnimation()
        getView!!.getShimmerLayout().visibility = android.view.View.GONE
        getView!!.getRecycler().visibility = android.view.View.VISIBLE
        if (programList.size == 0) view.getTxtNoPrograms().visibility = 0
    }

    fun setToolbarSettings(){
        getToolbarDrawerIcon?.visibility = android.view.View.GONE
        getToolbarText?.visibility = android.view.View.GONE
        getToolbarBack?.visibility = android.view.View.VISIBLE
        getToolbarLogo?.visibility = android.view.View.VISIBLE
    }

    fun openCommentPage(){
        Services.changeFragment(
            fragmentManager, CommentFragment(),
            "CommentFragment", "contentId", contentId
        )
    }

    fun getContentDetails(){
        Log.d("GetVideoData: ", "$stationId-$contentId-$stationTypes")

        stationType = when(stationTypes) {
            "audio" -> "audio"
            "radio" -> "audio"
            "video" -> "video"
            else -> {
                "video"
            }
        }

        //Set the visibility if contentId is empty or null
        if (contentId == "null") {
            setVisibilityContent()
        }

        try {
            getRepositories?.getContentDetails(
                getSessionManager?.getToken()!!,
                contentId.toString(),
                object :
                    RetrofitService.ResultHandler<ContentDetailsResponse> {
                    override fun onSuccess(data: ContentDetailsResponse?) {
                        try {
                            setTimerAnalytics(data)
                            //Save to model for sharing the content link
                            getContentDetails = data
                            //Parsing the video id from the youtube link
                            streamURL =
                                if (data!!.getData()!!.contentUrl.toString().contains("watch?v=")) {
                                    Services.getVideoId(data.getData()!!.contentUrl).toString()
                                } else {
                                    data.getData()!!.contentUrl.toString()
                                }

                            setVisibilityComponents(
                                data.getData()?.program?.name.toString(),
                                data.getData()!!.type.toString(),
                                data.getData()!!.format.toString(),
                                data.getData()!!.thumbnail.toString(),
                                data.getData()!!.broadcastDate.toString()
                            )
                            programID = data.getData()?.program?.id.toString()

                            if (getContentDetails?.getData()?.isFavorite!!)
                                view.getImageHeart().setImageDrawable(
                                    ContextCompat.getDrawable(
                                        view.context!!,
                                        R.drawable.ic_favorite_icon
                                    )
                                )
                            else
                                view.getImageHeart().setImageDrawable(
                                    ContextCompat.getDrawable(
                                        view.context!!,
                                        R.drawable.ic_favorite_no_outline_icon_gray
                                    )
                                )

                            getStreamDetails(
                                data.getData()!!.name.toString(),
                                data.getData()!!.description.toString(),
                                data.getData()!!.type.toString()
                            )

                            displayFeaturedStations(data.getData()?.program?.id.toString())
                        } catch (e: Exception) {
                            Log.d("ExceptionError: ", e.message.toString())
                        }
                    }

                    override fun onError(error: ContentDetailsResponse?) {
                        Log.d("ErrorContent", error?.getMessage()!!)
                        Toast.makeText(view.activity(), error.getMessage(), Toast.LENGTH_SHORT)
                            .show()
                        view.getAVLoading().visibility = android.view.View.GONE
                    }

                    override fun onFailed(message: String) {
                        Log.d("FailedContent", message)
                        Toast.makeText(
                            view.activity(),
                            "Something wen't wrong, Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                        view.getAVLoading().visibility = android.view.View.GONE
                    }
                })
        } catch (e: Exception) {
            e.message
        }
    }

    fun displayFeaturedStations(programId: String){
        startShimmerLayout()
        programList.clear()
        getRepositories?.stationContents(
            programId?.toInt(),
            4,
            stationType,
            getSessionManager?.getToken()!!,
            object :
                RetrofitService.ResultHandler<StationContentsResultModel> {
                override fun onSuccess(response: StationContentsResultModel?) {
                    programList.addAll(response?.data!!)
                    programsAdapter.notifyDataSetChanged()
                    stopShimmerLayout()
                }

                override fun onError(error: StationContentsResultModel?) {
                    Log.d("ErrorContent", error?.message!!)
                    stopShimmerLayout()
                }

                override fun onFailed(message: String) {
                    Log.d("FailedContent", message)
                    stopShimmerLayout()
                }
            })
    }

    @SuppressLint("WrongConstant")
    private fun setVisibilityContent() {

        //Showing the no stream available text and hiding the header layout
        view.getTextNoContent().visibility = 0
        view.getRelativeLayout().visibility = 8

        //Hiding the dropDown and live icon if the contentId is N/A or empty.
        view.getImgDropdownDesc().visibility = 8
        view.getImageHeart().visibility = 8
        view.getLayoutLiveHolder().visibility = 8
    }

    fun setVolumeEnabled(isVolumed: Boolean){
        val amanager = view.activity!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, isVolumed)
    }

    fun shareContent(){
        val buo = BranchObject.buo
        BranchObject.setLinkProperties(
            stationName.toString(),
            contentId.toString(),
            stationTypes.toString()
        )

        buo.generateShortUrl(
            view.context(), BranchObject.linkProperties!!
        ) { url, error ->
            println("shareContent: ${error == null}")
            val newUrl = if(error == null) url
            else "https://radyopilipino.app.link/${contentId}"
            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.putExtra(Intent.EXTRA_TEXT, newUrl)
            share.type = "text/plain"
            (view.context() as Activity).startActivity(Intent.createChooser(share, "Share via..."))
        }
    }

    @SuppressLint("SetTextI18n")
    fun setVisibilityComponents(
        channelName: String,
        streamType: String,
        streamFormat: String,
        thumbnailURL: String,
        broadcastDate: String
    ) {

        //Setting the visibility of the stream views
        if (streamFormat == "youtube") {
            initWebView()
        } else if (streamFormat == "audio" || streamFormat == "podcast") {
            initAudioView(thumbnailURL)
        } else if (streamFormat == "video") {
            initVideoPlayerView()
        }

        view.getTxtChannelDate().text = Services.convertDate(broadcastDate)
        //Hiding the live icon if the streamType is on-demand video
        if (streamType == "on-demand") {
            view.getTxtOnDemandDate().text = Services.convertDate(broadcastDate)
            view.getTxtOnDemandDate().visibility = android.view.View.VISIBLE
            view.getLayoutLiveHolder().visibility = android.view.View.GONE
        }
        //Showing the live indicator if the streamType is stream
        else if(streamType == "stream") {
            view.getTxtOnDemandDate().visibility = android.view.View.GONE
            view.getLayoutLiveHolder().visibility = android.view.View.VISIBLE }
    }

    private fun initWebView() {
        view.getAVLoading().visibility = android.view.View.GONE
        view.getWebView().visibility = android.view.View.VISIBLE
        view.getAudioStreamView().visibility = android.view.View.GONE
        view.getVideoPlayer().visibility = android.view.View.GONE
        view.controlsContainer?.visibility = android.view.View.GONE
        view.getLinearLayout().visibility = android.view.View.GONE
        view.getImageFullScreen().visibility = android.view.View.GONE
        initVideoView()
    }

    private fun initAudioView(thumbnailURL: String){
        view.getAVLoading().visibility = android.view.View.GONE
        view.getWebView()!!.visibility = android.view.View.GONE
        view.getVideoPlayer()!!.visibility = android.view.View.GONE
        view.controlsContainer?.visibility = android.view.View.VISIBLE
        view.getLinearLayout()!!.visibility = android.view.View.VISIBLE
        view.getImageFullScreen()!!.visibility = android.view.View.VISIBLE
        view.getAudioStreamView()!!.visibility = android.view.View.VISIBLE

        log(thumbnailURL)

        if (thumbnailURL == "" || thumbnailURL == "null") {
            view.getAudioStreamView().setBackgroundResource(R.drawable.ic_radio_station)
        } else {
            Glide.with(view.context!!)
                .load(thumbnailURL)
                .centerCrop()
                .into(view.getAudioStreamView()!!)
        }
    }

    private fun initVideoPlayerView(){
        view.getAVLoading().visibility = android.view.View.GONE
        view.getWebView()!!.visibility = android.view.View.GONE
        view.getAudioStreamView()!!.visibility = android.view.View.GONE
        view.getVideoPlayer()!!.visibility = android.view.View.VISIBLE
        view.getLinearLayout()!!.visibility = android.view.View.VISIBLE
        view.controlsContainer?.visibility = android.view.View.VISIBLE
        view.getImageFullScreen()!!.visibility = android.view.View.VISIBLE

        view.getVideoPlayer()!!.disableControls()
        view.getVideoPlayer().setSource(Uri.parse(streamURL))
    }

    private fun initVideoView(){
        view.lifecycle.addObserver(view.getWebView())
        view.getWebView().enterFullScreen()
        view.getWebView().addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(streamURL, 0f)
            }
        })
    }

    fun getStreamDetails(streamTitle: String, description: String, streamType: String) {
        view.setStreamingDetails(streamTitle, description, streamType)
    }

    @SuppressLint("ClickableViewAccessibility")
    var seekBarListener = (android.view.View.OnTouchListener { p0, p1 ->
        if (getContentDetails!!.getData()!!.format == "audio" || getContentDetails!!.getData()!!.format == "podcast") {
            val position = (mediaPlayer.duration / 100) * view.getSeekBar().progress
            mediaPlayer.seekTo(position)
            view.getTextCurrentDuration().text = Services.getTotalTextDuration(mediaPlayer.currentPosition)
        } else if (getContentDetails!!.getData()!!.format == "video") {
            view.getVideoPlayer().seekTo(view.getSeekBar().progress * 1000)
        }

        false
    })

    @SuppressLint("ClickableViewAccessibility")
    var seekChangeListener = object : SeekBar.OnSeekBarChangeListener {

        private var mProgressAtStartTracking = 0
        private val SENSITIVITY = 0

        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            mProgressAtStartTracking = view.getSeekBar().progress
        }
        override fun onStartTrackingTouch(p0: SeekBar?) {}
        override fun onStopTrackingTouch(p0: SeekBar?) {
            if(Math.abs(mProgressAtStartTracking - view.getSeekBar().progress) <= SENSITIVITY){
                view.getVideoPlayer().seekTo(view.getSeekBar().progress * 1000)
                val mCurrentPosition: Int = view.getVideoPlayer().getCurrentPosition() / 1000
                view.getTextCurrentDuration().text = DateUtils.formatElapsedTime((mCurrentPosition).toLong()).toString()
            }
        }
    }

    fun initializePlayer() =
        if (getContentDetails!!.getData()!!.format == "audio" || getContentDetails!!.getData()!!.format == "podcast")
            initializeMediaPlayer()
        else initializeVideoPlayer()

    @SuppressLint("WrongConstant")
    private fun initializeVideoPlayer(){
        notifService?.mediaPlayer?.stopMediaPlayer()
        if (view.getVideoPlayer().isPlaying()) {
            view.getVideoPlayer().pause()
            view.changePlayerIcon(R.drawable.ic_baseline_play_circle_outline_24)
        } else {
            view.getVideoPlayer().start()
            getVideoPlayerDuration()
            view.changePlayerIcon(R.drawable.ic_white_pause_circle_outline_24)
            view.controlsContainer?.visibility = 8
        }
        changeSeekBarDuration()
    }

    private fun getVideoPlayerDuration(){
        val mCurrentPosition: Int = view.getVideoPlayer().getDuration() / 1000

        view.getTextTotalDutaion().text = DateUtils.formatElapsedTime((mCurrentPosition).toLong()).toString()
        //view.getTextCurrentDuration().text =  DateUtils.formatElapsedTime((mCurrentPosition).toLong()).toString()
        view.getSeekBar().max = view.getVideoPlayer().getDuration() / 1000
    }

    private fun initializeMediaPlayer(){
        if (mediaPlayer.isPlaying) {
            //  durationHandler.removeCallbacks(runnable!!)
            view.changePlayerIcon(R.drawable.ic_baseline_play_circle_outline_24)
            mediaPlayer.pause()
            isPaused = true
            changeSeekBarDuration()
        } else try {
            if (!isPaused) {
                initMediaType()
            } else if (isPaused) {
                mediaPlayer.start()
                isPaused = false
            }
            changeSeekBarDuration()
            view.changePlayerIcon(R.drawable.ic_white_pause_circle_outline_24)
        } catch (e: IllegalArgumentException) {
        } catch (e: IllegalStateException) {
        } catch (e: IOException) {
        }

        mediaPlayer.setOnErrorListener { mp, what, extra ->
            mp.reset()
            false
        }

        mediaPlayer.setOnPreparedListener { mp ->
            try {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.pause()
                }
                mediaPlayer.start()
                view.changePlayerIcon(R.drawable.ic_white_pause_circle_outline_24)

                val totalTextDuration = Services.getTotalTextDuration(mediaPlayer.duration) // duration in time in millis
                val totalDuration = Services.getTotalDuration(mediaPlayer.duration)
                val duration: Int = mediaPlayer.currentPosition / 1000 // duration in time in millis
                val currentDuration = DateUtils.formatElapsedTime((duration).toLong()).toString()

                //Setup for total duration of seekbar for on-demand type
                if (getContentDetails!!.getData()!!.type!! == "stream")
                    view.setMediaPlayerDetails(currentDuration, totalTextDuration, 0)
                else
                    view.setMediaPlayerDetails(currentDuration, totalTextDuration, totalDuration)
                changeSeekBarDuration()
            } catch (e: Exception) {
                print(e.message)
            }
        }

        mediaPlayer.setOnBufferingUpdateListener { _: MediaPlayer, progress: Int ->
            view.setAudioSecondaryProgress(progress)
        }
    }

    private fun initMediaType() {
        if (streamURL != "") {
            mediaPlayer.setDataSource(streamURL)
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.prepareAsync()
        }
    }

    fun changeSeekBarDuration() {
        try {
            //Settings the seekbar for both audio and video player view.
            if (getContentDetails!!.getData()!!.format == "audio" || getContentDetails!!.getData()!!.format == "podcast") {
                if (mediaPlayer.isPlaying) {
                    runnable = Runnable {
                        changeSeekBarDuration()
                        val mCurrentPosition: Int = mediaPlayer.currentPosition / 1000
                        view.getTextCurrentDuration().text = DateUtils.formatElapsedTime((mCurrentPosition).toLong()).toString()
                        view.getSeekBar()!!.progress = mCurrentPosition
                    }
                    durationHandler.postDelayed(runnable!!, 1000)
                }
            } else {
                if (view.getVideoPlayer().isPlaying()) {
                    runnable = Runnable {
                        changeSeekBarDuration()
                        val mCurrentPosition: Int = view.getVideoPlayer().getCurrentPosition() / 1000
                        view.getTextCurrentDuration().text = DateUtils.formatElapsedTime((mCurrentPosition).toLong()).toString()
                        view.getSeekBar()!!.progress = mCurrentPosition
                    }
                    durationHandler.postDelayed(runnable!!, 1000)
                }
            }
        } catch (e: Exception) {
            e.message
        }
    }

    fun stopMediaPlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }

    fun getStationId() : Int {
        return getContentDetails!!.getData()!!.id!!
    }

    fun getAds() {
        val stationId: String? = view.arguments!!.getString("stationId")
        getRepositories?.getAds(getSessionManager?.getToken()!!,
            "content", stationId!!.toInt(),
            object : RetrofitService.ResultHandler<AdsModel> {
                override fun onSuccess(data: AdsModel?) {
                    println("getAds: ${Gson().toJson(data)}")
                    data?.data?.forEach { ads ->
                        if (ads.active!!) {
                            if (ads.section == "content" && ads.type == "banner") {
                                var strDateFrom = Services.convertDate(
                                    ads.durationFrom, "" +
                                            "yyyy-MM-dd", "yyyy/MM/dd"
                                )
                                var strDateEnd = Services.convertDate(
                                    ads.durationTo, "" +
                                            "yyyy-MM-dd", "yyyy/MM/dd"
                                )

                                val dateFormat = SimpleDateFormat("yyyy/MM/dd")
                                try {
                                    val dateFrom: Date? = dateFormat.parse(strDateFrom)
                                    val dateEnd: Date? = dateFormat.parse(strDateEnd)
                                    if (Services.dateBetween(
                                            Services.getCurrentDate(),
                                            dateFrom,
                                            dateEnd
                                        )
                                    ) {
                                        view.onBannerReady(ads)
                                    }
                                } catch (e: ParseException) {
                                }
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
            object : RetrofitService.ResultHandler<GeneralResultModel> {
                override fun onSuccess(data: GeneralResultModel?) {
                    if (isStream) {
                        changeImageAnimation(R.drawable.ic_favorite_no_outline_icon_gray)
                        getContentDetails?.getData()?.isFavorite = false
                    } else displayFeaturedStations(getContentDetails?.getData()?.program?.id.toString())
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
        animOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                view.getImageHeart().setImageDrawable(
                    ContextCompat.getDrawable(
                        view.context!!,
                        icon
                    )
                )
                view.getImageHeart().startAnimation(animIn)
            }
        })
        view.getImageHeart().startAnimation(animOut)
    }

    fun addToFavorites(contentId: String, isStream: Boolean) {
        getRepositories?.addToFavorites(
            contentId,
            getSessionManager?.getToken()!!,
            object : RetrofitService.ResultHandler<GeneralResultModel> {
                override fun onSuccess(data: GeneralResultModel?) {
                    if (isStream) {
                        changeImageAnimation(R.drawable.ic_favorite_icon)
                        getContentDetails?.getData()?.isFavorite = true
                    } else displayFeaturedStations(getContentDetails?.getData()?.program?.id.toString())
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
            "select_ad"
        )
    }

    fun checkRestriction(buttonId: Int): Boolean {
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

    interface View : AbstractPresenter.AbstractView{
        fun getRecycler() : RecyclerView
        fun setMediaPlayerDetails(
            currentDuration: String,
            totalTextDuration: String,
            totalDuration: Int
        )
        fun setAudioProgress(currentProgress: Int, mediaPlayer: MediaPlayer)
        fun setAudioSecondaryProgress(secondaryProgress: Int)
        fun changePlayerIcon(drawableIcon: Int)
        fun setStreamingDetails(title: String, subTitle: String, streamType: String)
        fun getTextTitle() : TextView
        fun getTextNoContent() : TextView
        fun getRelativeLayout() : RelativeLayout
        fun getWebView() : YouTubePlayerView
        fun getTextCurrentDuration() : TextView
        fun getTextTotalDutaion() : TextView
        fun getTxtOnDemandDate() : TextView
        fun getTxtChannelDate() : TextView
        fun getTxtNoPrograms() : TextView
        fun getAVLoading() : ProgressBar
        fun getAudioStreamView() : ImageView
        fun getBannerAds() : ImageView
        fun getImageVolume() : ImageView
        fun getImageFullScreen() : ImageView
        fun getImgDropdownDesc() : ImageView
        fun getImageHeart() : ImageView
        fun getShimmerLayout() : ShimmerLayout
        fun getSeekBar() : SeekBar
        fun getLinearLayout() : LinearLayout
        fun getLayoutLiveHolder() : LinearLayout
        fun getVideoPlayer() : BetterVideoPlayer
    }

    interface FeaturedHandler {
        fun setNoPrograms()
    }
}