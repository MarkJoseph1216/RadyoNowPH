package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.featured

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.model.stations.*
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.utils.*
import com.radyopilipinomediagroup.radyo_now.utils.Services.Companion.checkIfAuthenticated
import com.radyopilipinomediagroup.radyo_now.utils.Services.Companion.signOutExpired
import io.supercharge.shimmerlayout.ShimmerLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.net.URL

class FeaturedPresenter(var view: FeaturedFragment) : AbstractPresenter<FeaturedFragment>(view) {

    private var programs: MutableList<StationProgramResultModel.Data>? = mutableListOf()
    private var live: MutableList<StationContentsResultModel.Data>? = mutableListOf()
    private var featuredStations: MutableList<StationListResultModel.Station>? = mutableListOf()
    private var livesLLM =
        LinearLayoutManager(view.context(), LinearLayoutManager.HORIZONTAL, false)
    private var programsLLM = LinearLayoutManager(view.context())
    private var currentStationPosition: Int? = null
    private var context = view.context()

    private var serviceIntent: Intent? = null
    private var isPaused = false
    private var isNextPrev = false

    var notifBuilder: NotificationBuilder? = null
    var notifService : NotificationService? = null
    var liveAdapter: LiveListAdapter? = null
    var programsAdapter: ProgramListAdapter? = null

    var totalPage: Int? = 1
    var currentPage: Int? = 1

    init {
        getFeaturedStationList(1)
        notifService = NotificationService()
    }

    fun getBroadcastReceiverData(audioControls: String) {
        when (audioControls) {
            //Reading the live data coming from event bus.
            "Play" -> notifService?.mediaPlayer?.startAudioPlayer()
            "Pause" -> notifService?.mediaPlayer?.pauseAudioPlayer()
            "Previous" -> prevStation()
            "Next" -> nextStation()
            "Close" -> closeAudioPlayer()
        }
    }

    private fun closeAudioPlayer(){
        //Unregister the Event Bus and stop the audio player.
        //Reset the boolean to false.
        EventBus.getDefault().unregister(view.context)
        notifService?.mediaPlayer?.stopMediaPlayer()
        isNextPrev = false
        isPaused = false
    }

    private fun getFeaturedStationList(page: Int) {
        getRepositories?.getStationsList(
        page,
        getSessionManager?.getToken()!!,
        object : RetrofitService.ResultHandler<StationListResultModel> {
            override fun onSuccess(data: StationListResultModel?) {
                featuredStations?.addAll(data?.stations!!)
                totalPage = data?.meta?.pagination?.totalPages
                currentPage = data?.meta?.pagination?.currentPage
                view.featuredStationLoaded()
            }

            override fun onError(error: StationListResultModel?) {
                view.context?.toast(error?.message.toString())
                if (!checkIfAuthenticated(error?.message.toString())) {
                    signOutExpired(view.context(), getSessionManager)
                    view.activity().finish()
                }
            }
            override fun onFailed(message: String) { view.context?.toast(message) }
        })
    }

    fun displayCurrentStation(stationID: Int?) {
        try {
            for (index in 0 until featuredStations?.size!!) {
                if (stationID == featuredStations?.get(index)?.id) {
                    val first =
                        if (index == 0) android.view.View.GONE else android.view.View.VISIBLE
                    val last =
                        if (index == featuredStations?.size!! - 1) android.view.View.INVISIBLE else android.view.View.VISIBLE
                    currentStationPosition = index
                    view.updateFeaturedUI(featuredStations?.get(index), first, last)
                }
            }
            displayStationPrograms(stationID)
        } catch (e: Exception) {
        }
    }

    private fun displayPrograms(stationID: Int?) {
        programs?.clear()
        getRepositories?.stationPrograms(
            stationID!!,
            getSessionManager?.getToken()!!,
            object : RetrofitService.ResultHandler<StationProgramResultModel> {
                override fun onSuccess(data: StationProgramResultModel?) {
                    programs?.addAll(data?.data!!)
                    println("displayPrograms: ${Gson().toJson(programs)}")
                    programsAdapter = ProgramListAdapter(view.context!!, programs!!, stationID)
                    view.getProgramRecycler().adapter = programsAdapter
                    if (programs?.size == 0) view.noPrograms()
                    updateData()
                }

                override fun onError(error: StationProgramResultModel?) {
                    if (programs?.size == 0) view.noPrograms()
                    updateData()
                }

                override fun onFailed(message: String) {
                    if (programs?.size == 0) view.noPrograms()
                    updateData()
                }
            })
    }

    private fun displayStationPrograms(stationID: Int?) {
        startShimmerLayout()
        displayPrograms(stationID)
        view.hideNoData()
        getRepositories?.getTuneIn(
            stationID!!,
            getSessionManager?.getToken()!!,
            object : RetrofitService.ResultHandler<TuneInModelResultModel> {
                override fun onSuccess(data: TuneInModelResultModel?) {
                    live?.clear()
                    var convert : StationContentsResultModel.Data? = null
                    try {
                        convert = Gson().fromJson(Gson().toJson(data?.data?.get(0)?.liveContent!!),StationContentsResultModel.Data::class.java)
                        live?.add(convert!!)
                    }catch (e:Exception){
                        onFailed("error converting object")
                    }

                    println("displayLive: ${Gson().toJson(live)}")

                    liveAdapter = LiveListAdapter(view.context(), live, stationID)
                    view.getLiveRecycler().adapter = liveAdapter

                    if (live?.size == 0) view.noLive()
                    updateData()
                    stopShimmerLayout()
                }

                override fun onError(error: TuneInModelResultModel?) {
                    println("getTuneInError: ${error?.message}")
                    live?.clear()
                    liveAdapter = LiveListAdapter(view.context(), live, stationID)
                    view.getLiveRecycler().adapter = liveAdapter
                    if (live?.size == 0) view.noLive()
                    updateData()
                    stopShimmerLayout()
                }

                override fun onFailed(message: String) {
                    live?.clear()
                    liveAdapter = LiveListAdapter(view.context(), live, stationID)
                    view.getLiveRecycler().adapter = liveAdapter
                    println("getTuneInFailed: $message")
                    if (live?.size == 0) view.noLive()
                    updateData()
                    stopShimmerLayout()
                }
            })
    }

    private fun noDataUpdate() {
        live?.clear()
        programs?.clear()
        view.noLiveNoPrograms()
        updateData()
    }

    private fun updateData() {
        view.getProgramRecycler().adapter?.notifyDataSetChanged()
        view.getLiveRecycler().adapter?.notifyDataSetChanged()
    }

    fun initMediaType(streamUrl: String) {
        try {
            notifService?.mediaPlayer?.isReady = true
            if (streamUrl != "null" && streamUrl.isNotEmpty()) {
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
                    override fun onCompleted() {}
                }
                changeMediaPlayerSettings()
                notifService?.mediaPlayer?.initMediaType(streamUrl)
            } else notifService?.destroyNotification(view.activity())
        } catch (e: Exception) { }
    }

    private fun changeMediaPlayerSettings() {
        if (notifService?.mediaPlayer?.audioPlayer?.isPlaying == true) {
            notifService?.mediaPlayer?.pauseAudioPlayer()
            isPaused = true
            view.activity!!.runOnUiThread { createAudioNotificationPlayer() }
        } else try {
            if (isPaused) {
                notifService?.mediaPlayer?.startAudioPlayer()
                isPaused = false
            }
            view.activity!!.runOnUiThread { createAudioNotificationPlayer() }
        } catch (e: IllegalArgumentException) {
        } catch (e: IllegalStateException) {
        } catch (e: IOException) { }
    }

    private fun createAudioNotificationPlayer() = runBlocking {
        if (null == notifBuilder) notifBuilder = NotificationBuilder()
        //Check if the (isNextPrev) boolean is true then set the (isPaused) to false.
        //To display the play icon on notification.
        if (isNextPrev) isPaused = false
        serviceIntent = Intent(view.context, NotificationService::class.java)
        serviceIntent!!.action = STARTFOREGROUND_ACTION
        view.context!!.startService(serviceIntent)

        val url = URL(featuredStations!![currentStationPosition!!].logo.toString())
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
                featuredStations!![currentStationPosition!!].name.toString(),
                featuredStations!![currentStationPosition!!].description.toString(),
                featuredStations!![currentStationPosition!!].type.toString()
            )
        }
    }

    fun startShimmerLayout() {
        getView!!.getShimmerLayout()!!.visibility = android.view.View.VISIBLE
        getView!!.getShimmerLayout()!!.startShimmerAnimation()
        getView!!.getProgramRecycler()!!.visibility = android.view.View.GONE
    }

    fun stopShimmerLayout() {
        getView!!.getShimmerLayout()!!.stopShimmerAnimation()
        getView!!.getShimmerLayout()!!.visibility = android.view.View.GONE
        getView!!.getProgramRecycler()!!.visibility = android.view.View.VISIBLE
    }

    private fun updateStationSelection(position: Int?) {
        try {
            //Set the (isNextPrev) boolean to true to know that
            //the audio is being played and display the play icon
            //on creating the notification.
            isNextPrev = true
            displayCurrentStation(featuredStations?.get(position!!)?.id)
            view.passData(featuredStations?.get(position!!)?.id)
        } catch (e: Exception) { }
        if (currentPage!! < totalPage!!) {
            currentPage = currentPage!! + 1
            getFeaturedStationList(currentPage!!)
        }
    }

    fun nextStation() {
        updateStationSelection(currentStationPosition?.plus(1))
    }

    fun prevStation() {
        updateStationSelection(currentStationPosition?.minus(1))
    }

    fun displayProgram() {
        view.getProgramRecycler().layoutManager = programsLLM
        view.getLiveRecycler().layoutManager = livesLLM
    }

    fun setToolbarSettings() {
        getToolbarDrawerIcon!!.visibility = android.view.View.GONE
        getToolbarBack!!.visibility = android.view.View.GONE
    }

    fun statusBarWhite() {
        Services.setStatusBarLight(view.activity())
    }

    fun statusBarOriginal() {
        Services.setStatusBarOriginal(view.activity())
    }

    interface View : AbstractView {
        fun getLiveRecycler(): RecyclerView
        fun getProgramRecycler(): RecyclerView
        fun getShimmerLayout(): ShimmerLayout
        fun getLinearLayout(): LinearLayout
        fun updateFeaturedUI(featured: StationListResultModel.Station?, first: Int?, last: Int?)
        fun featuredStationLoaded()
        fun noPrograms()
        fun noLive()
        fun noLiveNoPrograms()
        fun hideNoData()
    }
}