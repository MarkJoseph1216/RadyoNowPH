package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.featured

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.stations.StationListResultModel
import com.radyopilipinomediagroup.radyo_now.ui.AbstractInterface
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardPresenter
import com.radyopilipinomediagroup.radyo_now.utils.NotificationService
import io.supercharge.shimmerlayout.ShimmerLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FeaturedFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>, FeaturedPresenter.View, AbstractInterface.DataHandler<Int?>, View.OnClickListener {

    private var stationView : View? = null
    private var stationLogo : CircularImageView? = null
    private var stationLiveRecycler: RecyclerView? = null
    private var stationProgramsRecycler: RecyclerView? = null
    private var presenter : FeaturedPresenter? = null
    private var startStation: Int? = null
    private var stationPrev: ImageView? = null
    private var stationNext: ImageView? = null
    private var layoutLiveHolder: LinearLayout? = null
    private var shimmerLayoutProgramList : ShimmerLayout? = null
    private var stationLiveContainer : LinearLayout? = null
    private var stationProgramContainer : LinearLayout? = null
    private var noPrograms : TextView? = null
    private var noLiveText : TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        stationView = inflater.inflate(R.layout.fragment_stations, container, false)
        initInitialize()
        initComponents()
        return stationView
    }

    private fun initComponents() {
        presenter?.statusBarWhite()
        presenter?.appBarHide()
        presenter?.displayProgram()
        presenter?.setToolbarSettings()
        initListener()
        hideNoData()
    }

    private fun initInitialize() {
        noLiveText = stationView?.findViewById(R.id.noLiveText)
        noPrograms = stationView?.findViewById(R.id.noPrograms)
        stationLiveContainer = stationView?.findViewById(R.id.stationLiveContainer)
        stationProgramContainer = stationView?.findViewById(R.id.stationProgramContainer)
        shimmerLayoutProgramList = stationView?.findViewById(R.id.shimmerLayoutProgramList)
        stationLiveRecycler = stationView?.findViewById(R.id.stationLiveRecycler)
        stationProgramsRecycler = stationView?.findViewById(R.id.stationProgramsRecycler)
        layoutLiveHolder = stationView?.findViewById(R.id.layoutLiveHolder)
        stationLogo = stationView?.findViewById(R.id.stationLogo)
        stationPrev = stationView?.findViewById(R.id.stationPrev)
        stationNext = stationView?.findViewById(R.id.stationNext)
        presenter = FeaturedPresenter(this)

        //Register the Event Bus to start the service
        EventBus.getDefault().register(this)
    }

    private fun initListener() {
        stationNext?.setOnClickListener(this::onClick)
        stationPrev?.setOnClickListener(this::onClick)
    }


    override fun activity(): FragmentActivity {
        return activity!!
    }

    override fun context(): Context {
        return context!!
    }

    override fun applicationContext(): Context {
        return activity().applicationContext
    }

    override fun getLiveRecycler(): RecyclerView {
        return stationLiveRecycler!!
    }

    override fun getProgramRecycler(): RecyclerView {
        return stationProgramsRecycler!!
    }

    override fun getShimmerLayout(): ShimmerLayout {
        return shimmerLayoutProgramList!!
    }

    override fun getLinearLayout(): LinearLayout {
        return layoutLiveHolder!!
    }

    override fun updateFeaturedUI(featured: StationListResultModel.Station?, first: Int?, last: Int?) {
        try {
            if (featured?.broadwave.toString() != "null" || featured?.broadwave.toString() != "") {
                presenter?.initMediaType(featured?.broadwave.toString())
            } else presenter?.notifService?.mediaPlayer?.stopMediaPlayer()
        } catch (e : Exception) {}

        stationNext?.visibility = last!!
        stationPrev?.visibility = first!!
        Glide.with(context!!)
            .load(featured?.logo)
            .placeholder(R.drawable.ic_signup)
            .into(stationLogo!!)
    }

    override fun featuredStationLoaded() {
        presenter?.displayCurrentStation(startStation)
    }

    override fun noPrograms() {
        stationProgramContainer?.visibility = View.GONE
    }

    override fun noLive() {
//        stationLiveContainer?.visibility = View.GONE
        noLiveText?.visibility = View.VISIBLE
        stationLiveRecycler?.visibility = View.GONE
    }

    override fun noLiveNoPrograms() {
        stationLiveContainer?.visibility = View.GONE
        stationProgramContainer?.visibility = View.GONE
        noPrograms?.visibility = View.VISIBLE
    }

    override fun hideNoData() {
        stationProgramContainer?.visibility = View.VISIBLE
        stationLiveContainer?.visibility = View.VISIBLE
        noPrograms?.visibility = View.GONE

        noLiveText?.visibility = View.GONE
        stationLiveRecycler?.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.appBarShow()
        presenter?.statusBarOriginal()
    }

    override fun onPause() {
        super.onPause()
        presenter?.appBarShow()
        presenter?.statusBarOriginal()
    }

    override fun onResume() {
        super.onResume()
        presenter?.appBarHide()
        presenter?.statusBarWhite()
    }

    override fun onStop() {
        super.onStop()
        presenter?.appBarShow()
        presenter?.statusBarOriginal()
    }

    override fun passData(data: Int?) {
        startStation = data
    }

    override fun getBackStacked() {}

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.stationNext -> presenter?.nextStation()
            R.id.stationPrev -> presenter?.prevStation()
        }
    }

    //Observable Data coming from notification service class.
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(audioControls: String) {
        presenter?.getBroadcastReceiverData(audioControls)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Terminate the notification and stopMediaPlayer
        presenter?.notifService?.destroyNotification(view!!.context)
        EventBus.getDefault().unregister(this)
    }
}