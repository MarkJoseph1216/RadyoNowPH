package com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.channel

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.model.stations.*
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.featured.LiveListAdapter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.featured.ProgramListAdapter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.StationsFragment
import com.radyopilipinomediagroup.radyo_now.utils.*
import com.radyopilipinomediagroup.radyo_now.utils.Services.Companion.checkIfAuthenticated
import com.radyopilipinomediagroup.radyo_now.utils.Services.Companion.signOutExpired
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.lang.Exception
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ChannelPresenter(var view: ChannelFragment) : AbstractPresenter<ChannelFragment>(view) {

    val fragmentManager = (view.context as FragmentActivity).supportFragmentManager
    val context = view.context!!
    var channelProgramList = mutableListOf<StationProgramResultModel.Data>()
    var live = mutableListOf<StationContentsResultModel.Data>()
    var LLMHorizontal = LinearLayoutManager(view.context(), LinearLayoutManager.HORIZONTAL, false)
    var LLMVertical = LinearLayoutManager(view.context())
    var liveAdapter: LiveListAdapter? = null
    var programsAdapter: ProgramListAdapter? = null
    var notifBuilder: NotificationBuilder? = null
    var notifService : NotificationService? = null

    private var isPaused = false
    private var mediaPlayer = MediaPlayer()
    private var resultModel: StationDetailsResultModel? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private var serviceIntent: Intent? = null

    init {
        setupToolbarSettings()
        setRecyclers()
        firebaseAnalytics = Firebase.analytics
        notifService = NotificationService()
    }

    private fun setupToolbarSettings(){
        getToolbarText!!.visibility = android.view.View.GONE
        getToolbarDrawerIcon!!.visibility = android.view.View.GONE
        getToolbarBack!!.visibility = android.view.View.VISIBLE
        getToolbarLogo!!.visibility = android.view.View.VISIBLE
        getToolbarBack?.setOnClickListener {
            Services.changeFragment(
                fragmentManager,
                StationsFragment(),
                "StationsFragment"
            )
        }
    }

    fun getBroadcastReceiverData(audioControls: String) {
        when (audioControls) {
            //Reading the live data coming from event bus.
            "Play" -> startAudioPlayer()
            "Pause" -> pauseAudioPlayer()
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
        notifService?.mediaPlayer?.pauseAudioPlayer()
        view.changePlayerIcon(R.drawable.ic_play_logo)
    }

    private fun stopAudioPlayer(){
        isPaused = false
        notifService?.mediaPlayer?.stopMediaPlayer()
        view.changePlayerIcon(R.drawable.ic_play_logo)
    }

    fun getStation(id: Int?) {
        Log.d("GetStation", "$id ${getSessionManager?.getToken()} asdasdasd")
        getRepositories?.getStationDetails(
        id,
        getSessionManager?.getToken()!!,
        object : RetrofitService.ResultHandler<StationDetailsResultModel> {
            override fun onSuccess(data: StationDetailsResultModel?) {
                Log.d("StationDetailsResult", Gson().toJson(data) + " asd")
                resultModel = data
                view.updateChannelDDetails(data)
                checkBroadwaveUrlNull()
            }
            override fun onError(error: StationDetailsResultModel?) {
                if (!checkIfAuthenticated(error?.message.toString())) {
                    signOutExpired(view.context(), getSessionManager)
                    view.activity().finish()
                }
            }
            override fun onFailed(message: String) {}
        })
    }

    fun initializeMediaPlayer() {
        try {
            changeImagePlayAppearance()
            notifService?.mediaPlayer?.isReady = true
            notifService?.mediaPlayer?.callback = object : AudioPlayer.MediaPlayerCallBack {
                override fun onPrepared() {
                    when(notifService?.mediaPlayer?.isReady) {
                        true -> {
                            notifService?.mediaPlayer?.stopMediaPlayer()
                            notifService?.mediaPlayer?.startAudioPlayer()
                        }
                    }
                }
                override fun onBuffered(progress: Int) {}
                override fun onCompleted() = view.changePlayerIcon(R.drawable.ic_play_logo)
            }
        } catch (e: Exception) { log(e.message.toString()) }
    }

    private fun changeImagePlayAppearance() =
        if (notifService?.mediaPlayer!!.audioPlayer.isPlaying) {
            //  durationHandler.removeCallbacks(runnable!!)
            view.changePlayerIcon(R.drawable.ic_play_logo)
            notifService?.mediaPlayer!!.pauseAudioPlayer()
            isPaused = true
            view.activity!!.runOnUiThread { createAudioNotificationPlayer() }
        } else try {
            if (!isPaused) {
                initMediaType()
            } else if (isPaused) {
                notifService?.mediaPlayer!!.startAudioPlayer()
                isPaused = false
            }
            view.changePlayerIcon(R.drawable.ic_pause_icon)
            view.activity!!.runOnUiThread { createAudioNotificationPlayer() }
        } catch (e: IllegalArgumentException) {
        } catch (e: IllegalStateException) {
        } catch (e: IOException) { }

    private fun createAudioNotificationPlayer() = runBlocking  {
        if (null == notifBuilder) notifBuilder = NotificationBuilder()
        serviceIntent = Intent(view.context, NotificationService::class.java)
        serviceIntent!!.action = STARTFOREGROUND_ACTION
        view.context!!.startService(serviceIntent)

        val url = URL(resultModel?.data?.logo.toString())
        withContext(Dispatchers.IO) {
            try {
                val input = url.openStream()
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                null
            }
        }?.let { bitmap ->
            notifBuilder!!.showMusicPlayerNotification(
                view.context(),
                isPaused,
                bitmap,
                resultModel?.data?.name.toString(),
                resultModel?.data?.description.toString(),
                resultModel?.data?.type.toString()
            )
        }
    }

    private fun initMediaType() {
        notifService?.mediaPlayer?.initMediaType(resultModel?.data?.broadwaveUrl)
    }

    private fun checkBroadwaveUrlNull(){
        if (resultModel?.data?.broadwaveUrl == "null" || resultModel?.data?.broadwaveUrl == null) {
            view.setPlayVisibility(8)
        } else {
            view.setPlayVisibility(0)
        }
    }

    fun stopMediaPlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.pause()
        }
    }

    private fun setRecyclers() {
        programsAdapter = ProgramListAdapter(view.context(), channelProgramList, view.getStationID())
        liveAdapter = LiveListAdapter(view.context(), live, view.getStationID())
        view.getProgramsRecycler().adapter = programsAdapter
        view.getProgramsRecycler().layoutManager = LLMVertical
        view.getLiveRecycler().adapter = liveAdapter
        view.getLiveRecycler().layoutManager = LLMHorizontal
    }

    fun updateNoData(){
        channelProgramList.clear()
        updateAdapters()
    }

    fun updateAdapters() {
        view.getProgramsRecycler().adapter?.notifyDataSetChanged()
        view.getLiveRecycler().adapter?.notifyDataSetChanged()
    }

    fun displayPrograms(id: Int?) {
        view.showAll()
        getRepositories?.stationPrograms(id!!,getSessionManager?.getToken()!!,object : RetrofitService.ResultHandler<StationProgramResultModel>{
            override fun onSuccess(data: StationProgramResultModel?) {
                channelProgramList.clear()
                channelProgramList.addAll(data?.data!!)
                println("displayPrograms: ${Gson().toJson(channelProgramList)}")

                if (channelProgramList.size == 0) view.hidePrograms()
                updateAdapters()
            }

            override fun onError(error: StationProgramResultModel?) {
                if (channelProgramList.size == 0) view.hidePrograms()
                updateAdapters()
            }

            override fun onFailed(message: String) {
                if (channelProgramList.size == 0) view.hidePrograms()
                updateAdapters()
            }
        })
    }

    fun getAds() {
        getRepositories?.getAds(getSessionManager?.getToken()!!,
            "channel", view.stationId!!,
            object : RetrofitService.ResultHandler<AdsModel> {
                override fun onSuccess(data: AdsModel?) {
                    println("getAds: ${Gson().toJson(data)}")
                    data?.data?.forEach { ads ->
                        if (ads.active!!) {
                            if (ads.section == "channel" && ads.type == "popup" && PopupAdsStatus.channelSinglePage) {
                                PopupAdsStatus.channelSinglePage = false
                                view.onPopupReady(ads)
                            } else if (ads.section == "channel" && ads.type == "slider") view.onSliderReady(
                                ads
                            )
                            else if (ads.section == "channel" && ads.type == "banner") {
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
                    view.imgBannerAds?.visibility = android.view.View.GONE
                }

                override fun onFailed(message: String) {
                    println("getAdsFailed: $message")
                    view.imgBannerAds?.visibility = android.view.View.GONE
                }
            })
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

    var station: StationListResultModel.Station? = null

    interface View : AbstractView {
        fun updateChannelDDetails(data: StationDetailsResultModel?)
        fun getLiveRecycler(): RecyclerView
        fun getProgramsRecycler(): RecyclerView
        fun hideLive()
        fun setPlayVisibility(visible: Int)
        fun changePlayerIcon(drawableIcon: Int)
        fun hidePrograms()
        fun showAll()
        fun getStationID() : Int
    }
}