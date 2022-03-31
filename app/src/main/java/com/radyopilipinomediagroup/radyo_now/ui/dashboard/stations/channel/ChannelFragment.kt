package com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.channel

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.model.stations.StationContentsResultModel
import com.radyopilipinomediagroup.radyo_now.model.stations.StationDetailsResultModel
import com.radyopilipinomediagroup.radyo_now.ui.AbstractInterface
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardPresenter
import com.radyopilipinomediagroup.radyo_now.utils.NotificationService
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.capitalizeWords
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ChannelFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>, ChannelPresenter.View,
    StationDetailsHandler, View.OnClickListener, AbstractInterface.AdsInterface{

    var channelView : View? = null
    var presenter: ChannelPresenter? = null
    var channelLogo : ShapeableImageView? = null
    var channelName: TextView? = null
    var channelSubtitle: TextView? = null
    var channelDetails: TextView? = null
    var channelDescription: TextView? = null
    var channelPlaylistRecycler: RecyclerView? = null
    var channelLiveRecycler: RecyclerView? = null
    var dropdown: ImageView? = null
    var imgBannerAds: ImageView? = null
    var stationId : Int? = null
    var btnPlayAudio: ImageButton? = null
    var programContainer : LinearLayout? = null
    var liveContainer : LinearLayout? = null
    var glide : Glide? = null
    var noLiveText : TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        channelView = inflater.inflate(R.layout.fragment_channel, container, false)
        initInitialize()
        initComponents()
        initListener()
        return channelView
    }

    private fun initInitialize() {
        imgBannerAds = channelView?.findViewById(R.id.imgBannerAds)
        noLiveText = channelView?.findViewById(R.id.noLiveText)
        liveContainer = channelView?.findViewById(R.id.liveContainer)
        programContainer = channelView?.findViewById(R.id.programContainer)
        channelLiveRecycler = channelView?.findViewById(R.id.channelLiveRecycler)
        dropdown = channelView?.findViewById(R.id.dropdown)
        channelLogo = channelView?.findViewById(R.id.channelLogo)
        channelName = channelView?.findViewById(R.id.channelName)
        channelSubtitle = channelView?.findViewById(R.id.channelSubtitle)
        btnPlayAudio = channelView?.findViewById(R.id.btnPlayAudio)
        channelDetails = channelView?.findViewById(R.id.channelDetails)
        channelDescription = channelView?.findViewById(R.id.channelDescription)
        channelPlaylistRecycler = channelView?.findViewById(R.id.channelPlaylistRecycler)
        presenter = ChannelPresenter(this)

        //Register the Event Bus to start the service
        EventBus.getDefault().register(this)
    }

    private fun initComponents() {
        glide = Glide.get(context())
        presenter?.getStation(stationId)
        presenter?.displayPrograms(stationId)
        hideDescription()
        dropdown?.setOnClickListener {
            if(channelDescription?.visibility == View.GONE)  showDescription()
            else hideDescription()
        }
        presenter?.getAds()
    }

    private fun initListener(){
        btnPlayAudio?.setOnClickListener(this::onClick)
    }

    private fun hideDescription(){
        channelDetails?.visibility = View.VISIBLE
        channelDescription?.visibility =  View.GONE
        dropdown?.rotation = 0F
    }

    private fun showDescription(){
        channelDetails?.visibility = View.INVISIBLE
        channelDescription?.visibility =  View.VISIBLE
        dropdown?.rotation = 180F
    }

    override fun activity(): FragmentActivity {
        return activity!!
    }

    override fun context(): Context {
       return context!!
    }

    override fun applicationContext(): Context {
        return  activity().applicationContext
    }

    override fun stationDetails(data: Int) {
        stationId = data
    }

    override fun updateChannelDDetails(data: StationDetailsResultModel?) {
        Glide.with(glide?.context!!)
            .load(data?.data?.logo)
            .placeholder(R.drawable.ic_signup)
            .into(channelLogo!!)
        channelName?.text = data?.data?.name

        channelSubtitle?.text = if(data?.data?.type?.equals("tv", true)!!) "TV" else data.data?.type?.capitalizeWords()
//        when(data?.data?.type) {
//            "Tv" -> channelSubtitle?.text = "TV"
//            else -> channelSubtitle?.text = data?.data?.type?.capitalizeWords()
//        }

        presenter?.live?.clear()
        var convert : StationContentsResultModel.Data? = null
        try {
            convert = Gson().fromJson(Gson().toJson(data.data?.liveContent!!),StationContentsResultModel.Data::class.java)
            presenter?.live?.add(convert!!)
        }catch (e:Exception){}
        presenter?.updateAdapters()

        if(presenter?.live?.size == 0) hideLive()
        setHTMLText(channelDetails!!, data.data?.description!!)
        setHTMLText(channelDescription!!, data.data?.description!!)
//        channelDescription?.text = data?.data?.description
    }

    private fun setHTMLText(textView: TextView, description: String){
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(description)
        }
    }

    override fun getLiveRecycler(): RecyclerView = channelLiveRecycler!!
    override fun getProgramsRecycler(): RecyclerView = channelPlaylistRecycler!!
    override fun hideLive() {
        noLiveText?.visibility = View.VISIBLE
        channelLiveRecycler?.visibility = View.GONE
//        liveContainer?.visibility = View.GONE
    }

    override fun setPlayVisibility(visible: Int) {
        btnPlayAudio?.visibility = visible
    }

    override fun changePlayerIcon(drawableIcon: Int) {
        btnPlayAudio!!.setBackgroundResource(drawableIcon)
    }

    override fun hidePrograms() {
        programContainer?.visibility = View.GONE
    }

    override fun showAll() {
        liveContainer?.visibility = View.VISIBLE
        programContainer?.visibility = View.VISIBLE

        noLiveText?.visibility = View.GONE
        channelLiveRecycler?.visibility = View.VISIBLE
    }

    override fun getStationID(): Int = stationId!!

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btnPlayAudio -> presenter?.initializeMediaPlayer()
        }
    }

    override fun onPopupReady(data: AdsModel.Data) {
        Services.popUpAds(context!!, data)
    }

    override fun onSliderReady(data: AdsModel.Data) {
    }

    override fun onBannerReady(data: AdsModel.Data) {
        when(data.assets[0].imageUrl.toString()) {
            "null", "" -> imgBannerAds?.visibility = View.GONE
            else -> {
                imgBannerAds?.visibility = View.VISIBLE
                imgBannerAds?.setOnClickListener {
                    try {
                        presenter?.processDataAnalytics(data)
                    } catch (e: Exception) {}
                }
                Glide.with(presenter?.context!!).load(data.assets[0].imageUrl).into(imgBannerAds!!)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Terminate the notification and stopMediaPlayer
        presenter?.notifService?.destroyNotification(view!!.context)
        EventBus.getDefault().unregister(this)
    }

    //Observable Data coming from notification service class.
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(audioControls: String) {
        presenter?.getBroadcastReceiverData(audioControls)
    }
}