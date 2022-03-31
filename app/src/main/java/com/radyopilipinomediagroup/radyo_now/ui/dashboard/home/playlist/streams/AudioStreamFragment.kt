package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.streams

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.ui.AbstractInterface
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.favorites.FavoritesPresenter
import com.radyopilipinomediagroup.radyo_now.utils.NotificationService
import com.radyopilipinomediagroup.radyo_now.utils.Services
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AudioStreamFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>,
    AudioStreamPresenter.View, View.OnClickListener, AbstractInterface.AdsInterface, FavoritesPresenter.FavoriteCallback {

    private var audioView : View? = null
    private var fragmentManagers: FragmentManager? = null
    private var imgPlay: ImageView? = null
    private var txtViewComments: TextView? = null
    private var txtCurrentDuration: TextView? = null
    private var txtTotalDuration: TextView? = null
    private var txtHeaderDate : TextView? = null
    private var txtHeaderDesc : TextView? = null
    private var txtStationName : TextView? = null
    private var txtChannelName : TextView? = null
    private var txtChannelDate : TextView? = null
    private var txtChannelDesc : TextView? = null
    private var audioProgress: SeekBar? = null
    private var presenter: AudioStreamPresenter? = null
    private var txtNoPrograms: TextView? = null
    private var albumThumb : ImageView? = null
    private var imgOption : ImageView? = null
    private var imgBackward : ImageView? = null
    private var imgForward : ImageView? = null
    private var streamingRecycler: RecyclerView? = null
    private var imgShowDescription : ImageView? = null
    private var imgBannerAds: ImageView? = null
    private var layoutTitleHolder : LinearLayout? = null
    private var layoutSubtitleHolder : ScrollView? = null
    private var imgCloseDesc : ImageView? = null
    private var imgHeart : ImageView? = null
    private var next : ImageView? = null
    private var prev : ImageView? = null
    var glide: RequestManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        audioView = inflater.inflate(R.layout.fragment_audio_stream, container, false)
        initInitialize()
        initListener()
        return audioView
    }

    private fun initInitialize() {
        prev = audioView?.findViewById(R.id.prev)
        next = audioView?.findViewById(R.id.next)
        imgHeart = audioView?.findViewById(R.id.imgHeart)
        imgPlay = audioView?.findViewById(R.id.imgPlay)
        txtCurrentDuration = audioView?.findViewById(R.id.txtCurrentDuration)
        txtTotalDuration = audioView?.findViewById(R.id.txtTotalDuration)
        audioProgress = audioView?.findViewById(R.id.audioProgress)
        albumThumb = audioView?.findViewById(R.id.albumThumb)
        txtHeaderDate = audioView?.findViewById(R.id.txtHeaderDate)
        txtHeaderDesc = audioView?.findViewById(R.id.txtHeaderDesc)
        txtStationName = audioView?.findViewById(R.id.txtStationName)
        txtChannelName = audioView?.findViewById(R.id.txtChannelName)
        txtChannelDate = audioView?.findViewById(R.id.txtChannelDate)
        txtChannelDesc = audioView?.findViewById(R.id.txtChannelDesc)
        streamingRecycler = audioView?.findViewById(R.id.streamingRecycler)
        txtViewComments = audioView?.findViewById(R.id.txtViewComments)
        txtNoPrograms = audioView?.findViewById(R.id.txtNoPrograms)
        imgOption = audioView?.findViewById(R.id.imgOption)
        imgCloseDesc = audioView?.findViewById(R.id.imgCloseDesc)
        imgBackward = audioView?.findViewById(R.id.imgBackward)
        imgForward = audioView?.findViewById(R.id.imgForward)
        imgShowDescription = audioView?.findViewById(R.id.imgShowDescription)
        layoutTitleHolder = audioView?.findViewById(R.id.layoutTitleHolder)
        layoutSubtitleHolder = audioView?.findViewById(R.id.layoutSubtitleHolder)
        imgBannerAds = audioView?.findViewById(R.id.imgBannerAds)
        fragmentManagers = activity().supportFragmentManager
        presenter = AudioStreamPresenter(this)
        glide = Glide.with(context!!)
        //Register the Event Bus to start the service
        EventBus.getDefault().register(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        next?.setOnClickListener(this::onClick)
        prev?.setOnClickListener(this::onClick)
        imgPlay?.setOnClickListener(this::onClick)
        imgOption?.setOnClickListener(this::onClick)
        imgShowDescription?.setOnClickListener(this::onClick)
        imgCloseDesc?.setOnClickListener(this::onClick)
        imgBackward?.setOnClickListener(this::onClick)
        imgForward?.setOnClickListener(this::onClick)
        txtViewComments?.setOnClickListener(this::onClick)
        imgHeart?.setOnClickListener(this::onClick)
        audioProgress?.setOnTouchListener(presenter?.seekBarListener)
        audioProgress?.setOnSeekBarChangeListener(presenter?.seekChangeListener)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.imgPlay -> { presenter!!.initializeMediaPlayer() }
            R.id.imgOption -> { presenter?.showPopUpMenu()}
            R.id.imgBackward -> { presenter?.backwardForward(30000, "-")}
            R.id.imgForward -> {presenter?.backwardForward(30000, "+") }
            R.id.imgShowDescription -> setDescriptionVisibility(8, 0 , R.anim.exit_anim, R.anim.enter_anim)
            R.id.imgCloseDesc -> setDescriptionVisibility(0, 8, R.anim.enter_anim, R.anim.exit_anim)
            R.id.next -> presenter?.nextAudio()
            R.id.prev -> presenter?.prevAudio()
            R.id.txtViewComments -> presenter?.checkRestriction(R.id.txtViewComments)
            R.id.imgHeart -> presenter?.checkRestriction(R.id.imgHeart)
        }
    }

    private fun setDescriptionVisibility(titleVisible: Int, subtitleVisible: Int, titleAnim: Int, subTitleAnim: Int){
        layoutTitleHolder!!.visibility = titleVisible
        val titleAnim = AnimationUtils.loadAnimation(activity, titleAnim)
        layoutTitleHolder!!.startAnimation(titleAnim)

        layoutSubtitleHolder!!.visibility = subtitleVisible
        val subTitleAnim = AnimationUtils.loadAnimation(activity, subTitleAnim)
        layoutSubtitleHolder!!.startAnimation(subTitleAnim)
    }

    override fun activity(): FragmentActivity = activity!!
    override fun context(): Context = context!!
    override fun applicationContext(): Context = activity().applicationContext
    override fun getImageHeart(): ImageView = imgHeart!!

    override fun setMediaPlayerDetails(currentDuration: String, totalTextDuration: String, totalDuration: Int) {
        txtCurrentDuration!!.text = currentDuration
        txtTotalDuration!!.text = totalTextDuration
    }

    override fun setAudioSecondaryProgress(secondaryProgress: Int) {
        audioProgress!!.secondaryProgress = secondaryProgress
    }

    override fun changePlayerIcon(drawableIcon: Int) {
        imgPlay!!.setImageResource(drawableIcon)
    }

    override fun getImgOption(): ImageView {
        return imgOption!!
    }

    override fun getRecycler(): RecyclerView = streamingRecycler!!

    override fun getTxtNoPrograms(): TextView = txtNoPrograms!!

    override fun setStreamDetails(
        stationName: String,
        stationDate: String,
        thumbURL: String,
        streamURLResult: String,
        description: String
    ) {
        Glide.with(view!!.context)
            .load(thumbURL)
            .centerCrop()
            .placeholder(R.drawable.ic_no_image)
            .into(albumThumb!!)
        txtStationName!!.text = stationName
        txtChannelName!!.text = stationName
        txtHeaderDate!!.text = Services.convertDate(stationDate)
        txtChannelDate!!.text = Services.convertDate(stationDate)
        txtHeaderDesc!!.text = description
        txtChannelDesc!!.text = description
    }

    override fun getTextCurrentDuration(): TextView {
        return txtCurrentDuration!!
    }

    override fun getBannerAds(): ImageView {
        return imgBannerAds!!
    }

    override fun getSeekBar(): SeekBar {
        return audioProgress!!
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
        cancelTimer()
    }

    private fun cancelTimer(){
        if (presenter?.timer != null) {
            presenter?.timer?.cancel()
            presenter?.timer?.onFinish()
        }
    }

    override fun onPopupReady(data: AdsModel.Data) {}
    override fun onSliderReady(data: AdsModel.Data) {}
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
                glide?.load(data.assets[0].imageUrl)?.into(imgBannerAds!!)
            }
        }
    }

    override fun deleteFavorite(contentId: String) {
        presenter?.deleteFavorite(contentId,false)
    }

    override fun addToFavorites(contentId: String) {
        presenter?.addToFavorites(contentId,false)
    }

    fun checkRestriction(buttonId: Int): Boolean? = presenter?.checkRestriction(buttonId)
}