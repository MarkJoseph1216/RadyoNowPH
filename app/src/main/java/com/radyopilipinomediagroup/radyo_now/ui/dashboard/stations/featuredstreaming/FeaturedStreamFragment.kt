package com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.featuredstreaming

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AnimationUtils
import android.webkit.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.halilibo.bvpkotlin.BetterVideoPlayer
import com.halilibo.bvpkotlin.VideoCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.ui.AbstractInterface
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.favorites.FavoritesPresenter
import com.radyopilipinomediagroup.radyo_now.utils.Constants
import com.radyopilipinomediagroup.radyo_now.utils.NotificationService
import com.radyopilipinomediagroup.radyo_now.utils.Services
import io.supercharge.shimmerlayout.ShimmerLayout
import java.lang.reflect.Field
import java.lang.reflect.Method

class FeaturedStreamFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>,
    FeaturedPresenter.View, View.OnClickListener, AbstractInterface.OrientationHandler,
    AbstractInterface.AdsInterface, FavoritesPresenter.FavoriteCallback, FeaturedPresenter.FeaturedHandler {

    private var streamView: View? = null
    private var streamingRecycler: RecyclerView? = null
    private var presenter: FeaturedPresenter? = null
    private var imgPlay: ImageView? = null
    private var backward: ImageView? = null
    private var forward: ImageView? = null
    private var txtCurrentDuration: TextView? = null
    private var txtTotalDuration: TextView? = null
    private var txtViewComments: TextView? = null
    private var audioProgress: SeekBar? = null
    private var audioStreamView: ImageView? = null
    private var imgShowDescription: ImageView? = null
    private var videoStreamView: YouTubePlayerView? = null
    private var linearLayoutCenter: LinearLayout? = null
    private var layoutTitleHolder: LinearLayout? = null
    var controlsContainer: LinearLayout? = null
    private var layoutSubtitleHolder: LinearLayout? = null
    private var imgSettings: ImageView? = null
    private var imgVolume: ImageView? = null
    private var imgFullScreen: ImageView? = null
    private var imgCloseDesc: ImageView? = null
    private var layoutLiveHolder: LinearLayout? = null
    private var txtTitle: TextView? = null
    private var imgHeart: ImageView? = null
    private var txtChannelName: TextView? = null
    private var txtChannelDesc: TextView? = null
    private var streamLoading: ProgressBar? = null
    private var txtNoContent: TextView? = null
    private var txtOnDemandDate: TextView? = null
    private var txtChannelDate: TextView? = null
    private var txtNoPrograms: TextView? = null
    private var imgBannerAds: ImageView? = null
    private var layoutHeader: RelativeLayout? = null
    private var shimmerLayout: ShimmerLayout? = null
    private var videoPlayerView: BetterVideoPlayer? = null
    private var isMuted = false
    private var params: ViewGroup.LayoutParams? = null
    private var decorView: View? = null
    private var isFullScreen = true
    private var isOnStart = true
    private var orientationListener: OrientationEventListener? = null
    private var fullScreenListener: YouTubePlayerFullScreenListener? = null
    private var glide: RequestManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        streamView = inflater.inflate(R.layout.fragment_featured_stream, container, false)
        initDeclaration()
        initPresenter()
        initListener()
        return streamView
    }

    private fun initDeclaration() {
        glide = Glide.with(context!!)
        decorView = activity().window.decorView
        txtNoContent = streamView?.findViewById(R.id.txtNoContent)
        layoutHeader = streamView?.findViewById(R.id.layoutHeader)
        videoStreamView = streamView?.findViewById(R.id.videoStreamView)
        linearLayoutCenter = streamView?.findViewById(R.id.linearLayoutCenter)
        audioStreamView = streamView?.findViewById(R.id.audioStreamView)
        layoutLiveHolder = streamView?.findViewById(R.id.layoutLiveHolder)
        imgShowDescription = streamView?.findViewById(R.id.imgShowDescription)
        txtChannelDesc = streamView?.findViewById(R.id.txtChannelDesc)
        streamLoading = streamView?.findViewById(R.id.streamLoading)
        txtTitle = streamView?.findViewById(R.id.txtTitle)
        txtViewComments = streamView?.findViewById(R.id.txtViewComments)
        txtChannelName = streamView?.findViewById(R.id.txtChannelName)
        txtOnDemandDate = streamView?.findViewById(R.id.txtOnDemandDate)
        txtChannelDate = streamView?.findViewById(R.id.txtChannelDate)
        txtNoPrograms = streamView?.findViewById(R.id.txtNoPrograms)
        imgPlay = streamView?.findViewById(R.id.imgPlay)
        backward = streamView?.findViewById(R.id.backward)
        forward = streamView?.findViewById(R.id.forward)
        imgVolume = streamView?.findViewById(R.id.imgVolume)
        imgFullScreen = streamView?.findViewById(R.id.imgFullScreen)
        imgHeart = streamView?.findViewById(R.id.imgHeart)
        imgCloseDesc = streamView?.findViewById(R.id.imgCloseDesc)
        imgSettings = streamView?.findViewById(R.id.imgSettings)
        txtCurrentDuration = streamView?.findViewById(R.id.txtCurrentDuration)
        txtTotalDuration = streamView?.findViewById(R.id.txtTotalDuration)
        audioProgress = streamView?.findViewById(R.id.audioProgress)
        streamingRecycler = streamView?.findViewById(R.id.streamingRecycler)
        shimmerLayout = streamView?.findViewById(R.id.shimmerLayoutAllPlayList)
        controlsContainer = streamView?.findViewById(R.id.controlsContainer)
        videoPlayerView = streamView?.findViewById(R.id.videoPlayerView)
        layoutTitleHolder = streamView?.findViewById(R.id.layoutTitleHolder)
        layoutSubtitleHolder = streamView?.findViewById(R.id.layoutSubtitleHolder)
        imgBannerAds = streamView?.findViewById(R.id.imgBannerAds)
    }

    private fun initPresenter() {
        presenter = FeaturedPresenter(this)
        presenter?.setProgramsRecycler()
        presenter?.getContentDetails()
        presenter?.setToolbarSettings()
        presenter?.getAds()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        layoutHeader?.visibility = View.INVISIBLE
        imgPlay?.setOnClickListener(this::onClick)
        imgSettings?.setOnClickListener(this::onClick)
        audioProgress?.setOnTouchListener(presenter?.seekBarListener)
        audioProgress?.setOnSeekBarChangeListener(presenter?.seekChangeListener)
        imgShowDescription?.setOnClickListener(this::onClick)
        imgVolume?.setOnClickListener(this::onClick)
        imgFullScreen?.setOnClickListener(this::onClick)
        imgCloseDesc?.setOnClickListener(this::onClick)
        txtViewComments?.setOnClickListener(this::onClick)
        imgHeart?.setOnClickListener(this::onClick)
        backward?.setOnClickListener(this::onClick)
        forward?.setOnClickListener(this::onClick)
        videoPlayerView?.setOnClickListener(this::onClick)
        videoStreamView?.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                presenter?.notifService?.mediaPlayer?.stopMediaPlayer()
                exitFullScreen()
                videoStreamView?.exitFullScreen()
                videoStreamView?.addFullScreenListener(fullScreenListener!!)
                layoutHeader?.visibility = View.VISIBLE
            }
        })
        fullScreenListener = object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                enterFullScreen()
            }

            override fun onYouTubePlayerExitFullScreen() {
                exitFullScreen()
            }
        }
        orientationListener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (activity?.resources?.configuration?.orientation == 2) enterFullScreen()
                else {
                    isFullScreen = true
                    when(isOnStart) {
                        true -> isOnStart = false
                        false ->  exitFullScreen()
                    }
                }
            }
        }
        orientationListener?.enable()
        videoPlayerView?.setCallback(object : VideoCallback {
            override fun onStarted(player: BetterVideoPlayer) {}
            override fun onPaused(player: BetterVideoPlayer) {}
            override fun onPreparing(player: BetterVideoPlayer) {}
            override fun onPrepared(player: BetterVideoPlayer) {}
            override fun onBuffering(percent: Int) {}
            override fun onError(player: BetterVideoPlayer, e: Exception) {}
            @SuppressLint("WrongConstant")
            override fun onCompletion(player: BetterVideoPlayer) {
                controlsContainer?.visibility = 0
                changePlayerIcon(R.drawable.ic_baseline_play_circle_outline_24)
            }
            override fun onToggleControls(player: BetterVideoPlayer, isShowing: Boolean) {}
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        orientationListener?.disable()
        // Terminate the notification and stopMediaPlayer
        presenter?.notifService?.destroyNotification(view!!.context)
        presenter?.stopMediaPlayer()
        cancelTimer()
    }

    private fun cancelTimer(){
        if (presenter?.timer != null) {
            presenter?.timer?.cancel()
            presenter?.timer?.onFinish()
        }
    }

    private fun showPopUpMenu() {
        val wrapper = ContextThemeWrapper(
            view!!.context,
            R.style.popupMenu
        )
        val popup = PopupMenu(wrapper, imgSettings)
        popup.menuInflater?.inflate(R.menu.popup_menu_stream, popup.menu)
        val likeMenu = popup.menu.findItem(R.id.like)
        if(presenter?.getContentDetails?.getData()?.isFavorite!!) {
            likeMenu.title = "Unlike"
            likeMenu.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_favorite_icon)
        }
        else {
            likeMenu.title = "Like"
            likeMenu.icon =
                ContextCompat.getDrawable(context!!, R.drawable.ic_favorite_no_outline_icon_gray)
        }
        setForceShowIcon(popup)
        popup.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.addToPlaylist -> {
                    try {
                        (context as DashboardActivity).passData(presenter?.getStationId()!!)
                    } catch (e: Exception) {
                    }
                    return@setOnMenuItemClickListener true
                }
                R.id.shareContent -> {
                    presenter?.shareContent()
                    return@setOnMenuItemClickListener true
                }
                R.id.like -> {
                    if(checkRestriction(Constants.ADD_FAVORITES) == false)  return@setOnMenuItemClickListener false
                    presenter?.addDeleteFavorites()
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
        popup.show()
    }

    private fun setForceShowIcon(popupMenu: PopupMenu) {
        try {
            val fields: Array<Field> = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.getName()) {
                    field.isAccessible = true
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgPlay -> presenter!!.initializePlayer()
            R.id.imgSettings -> showPopUpMenu()
            R.id.videoPlayerView -> showControls()
            R.id.backward -> setCurrentPosition("backward")
            R.id.forward -> setCurrentPosition("forward")
            R.id.imgHeart -> presenter?.checkRestriction(R.id.imgHeart)
            R.id.imgShowDescription -> setDescriptionVisibility(
                8,
                0,
                R.anim.exit_anim,
                R.anim.enter_anim
            )
            R.id.txtViewComments -> presenter?.checkRestriction(R.id.txtViewComments)
            R.id.imgCloseDesc -> setDescriptionVisibility(0, 8, R.anim.enter_anim, R.anim.exit_anim)
            R.id.imgVolume -> {
                if (!isMuted) {
                    isMuted = true
                    presenter?.setVolumeEnabled(isMuted)
                    imgVolume!!.setImageResource(R.drawable.ic_volume_mute)
                } else {
                    isMuted = false
                    presenter?.setVolumeEnabled(isMuted)
                    imgVolume!!.setImageResource(R.drawable.ic_volume_unmute)
                }
            }
            R.id.imgFullScreen -> {
                if (isFullScreen) {
                    isFullScreen = false
                    enterVideoFullScreen()
                } else {
                    exitVideoFullScreen()
                    isFullScreen = true
                }
            }
        }
    }

    private fun setCurrentPosition(type: String){
        if (type == "backward") videoPlayerView?.getCurrentPosition()?.minus(5000)?.let { videoPlayerView?.seekTo(it) }
        else videoPlayerView?.getCurrentPosition()?.plus(5000)?.let { videoPlayerView?.seekTo(it) }
        videoPlayerView?.start()
        changePlayerIcon(R.drawable.ic_white_pause_circle_outline_24)
        presenter?.changeSeekBarDuration()
    }

    @SuppressLint("WrongConstant")
    private fun showControls() =
        if (controlsContainer?.visibility == 8) controlsContainer?.visibility = 0
    else controlsContainer?.visibility = 8

    private fun enterFullScreen() {
        if (presenter?.getContentDetails?.getData()?.format.toString() != "video" &&
            presenter?.getContentDetails?.getData()?.format.toString() != "null") {
            activity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            params = layoutHeader?.layoutParams
            params?.width = MATCH_PARENT
            params?.height = MATCH_PARENT
            layoutHeader?.layoutParams = params
        }
    }

    private fun enterVideoFullScreen() {
        activity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        params = layoutHeader?.layoutParams
        params?.width = MATCH_PARENT
        params?.height = MATCH_PARENT
        layoutHeader?.layoutParams = params
    }

    private fun exitFullScreen() {
        if (presenter?.getContentDetails?.getData()?.format.toString() != "video" &&
            presenter?.getContentDetails?.getData()?.format.toString() != "null") {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            params = layoutHeader?.layoutParams
            params?.width = MATCH_PARENT
            params?.height = WRAP_CONTENT
            layoutHeader?.layoutParams = params
        }
    }

    private fun exitVideoFullScreen() {
        activity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        params = layoutHeader?.layoutParams
        params?.width = MATCH_PARENT
        params?.height = 742
        layoutHeader?.layoutParams = params
    }

    private fun setDescriptionVisibility(
        titleVisible: Int,
        subtitleVisible: Int,
        titleAnim: Int,
        subTitleAnim: Int
    ) {
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
    override fun getRecycler(): RecyclerView = streamingRecycler!!
    override fun onDestroy() {
        super.onDestroy()
        presenter!!.stopMediaPlayer()
    }

    override fun setMediaPlayerDetails(
        currentDuration: String,
        totalTextDuration: String,
        totalDuration: Int
    ) {
        txtCurrentDuration!!.text = currentDuration
        txtTotalDuration!!.text = totalTextDuration
        if (totalDuration != 0)
            audioProgress!!.max = totalDuration
    }

    override fun setAudioProgress(currentProgress: Int, mediaPlayer: MediaPlayer) {
        audioProgress!!.progress = mediaPlayer.currentPosition
    }

    override fun setAudioSecondaryProgress(secondaryProgress: Int) {
        audioProgress!!.secondaryProgress = secondaryProgress
    }

    override fun changePlayerIcon(drawableIcon: Int) = imgPlay!!.setImageResource(drawableIcon)

    @SuppressLint("SetTextI18n")
    override fun setStreamingDetails(title: String, description: String, streamType: String) {
        txtTitle!!.text = title
        txtChannelName!!.text = title
        txtChannelDesc!!.text = description
    }

    override fun getTextTitle(): TextView {
        return txtTitle!!
    }

    override fun getTextNoContent(): TextView {
        return txtNoContent!!
    }

    override fun getRelativeLayout(): RelativeLayout {
        return layoutHeader!!
    }

    override fun getWebView(): YouTubePlayerView {
        return videoStreamView!!
    }

    override fun getTextCurrentDuration(): TextView {
        return txtCurrentDuration!!
    }

    override fun getTextTotalDutaion(): TextView {
        return txtTotalDuration!!
    }

    override fun getTxtOnDemandDate(): TextView {
        return txtOnDemandDate!!
    }

    override fun getTxtChannelDate(): TextView {
        return txtChannelDate!!
    }

    override fun getTxtNoPrograms(): TextView {
        return txtNoPrograms!!
    }

    override fun getAVLoading(): ProgressBar {
        return streamLoading!!
    }

    override fun getAudioStreamView(): ImageView {
        return audioStreamView!!
    }

    override fun getImgDropdownDesc(): ImageView {
        return imgShowDescription!!
    }

    override fun getBannerAds(): ImageView {
        return imgBannerAds!!
    }

    override fun getImageVolume(): ImageView {
        return imgVolume!!
    }

    override fun getImageFullScreen(): ImageView {
        return imgFullScreen!!
    }

    override fun getImageHeart(): ImageView {
        return imgHeart!!
    }

    override fun getShimmerLayout(): ShimmerLayout {
        return shimmerLayout!!
    }

    override fun getSeekBar(): SeekBar {
        return audioProgress!!
    }

    override fun getLinearLayout(): LinearLayout {
        return linearLayoutCenter!!
    }

    override fun getLayoutLiveHolder(): LinearLayout {
        return layoutLiveHolder!!
    }

    override fun getVideoPlayer(): BetterVideoPlayer {
        return videoPlayerView!!
    }

    override fun videoLandscape() {
        if (videoStreamView?.visibility == View.VISIBLE) {
            videoStreamView?.enterFullScreen()
        } else {
            enterFullScreen()
        }
    }

    override fun videoPortrait() {
        if (videoStreamView?.visibility == View.VISIBLE) {
            videoStreamView?.exitFullScreen()
        } else {
            exitFullScreen()
        }
    }

    override fun onPopupReady(data: AdsModel.Data) {}
    override fun onSliderReady(data: AdsModel.Data) {}
    override fun onBannerReady(data: AdsModel.Data) {
        when(data.assets[0].imageUrl.toString()) {
            "null" -> imgBannerAds?.visibility = View.GONE
            "" -> imgBannerAds?.visibility = View.GONE
            else -> {
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

    @SuppressLint("WrongConstant")
    override fun setNoPrograms() {
        txtNoPrograms?.visibility = 0
    }

    fun checkRestriction(buttonId: Int): Boolean? = presenter?.checkRestriction(buttonId)
}