package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.baoyz.widget.PullRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson
import com.mikhaellopez.circularimageview.CircularImageView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.GeneralResultModel
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistListResultModel
import com.radyopilipinomediagroup.radyo_now.model.programs.FeaturedProgramsResultModel
import com.radyopilipinomediagroup.radyo_now.model.stations.FeaturedResultModel
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractInterface
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.featured.FeaturedFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.PlaylistFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.channel.ChannelFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.channel.StationDetailsHandler
import com.radyopilipinomediagroup.radyo_now.utils.Constants
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.guestWarningDialog
import com.radyopilipinomediagroup.radyo_now.utils.toast
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import java.util.*

class HomePresenter(val view: HomeFragment): AbstractPresenter<HomeFragment>(view) {

    private var posts = mutableListOf<FeaturedProgramsResultModel.Data>()
    private var featured = mutableListOf<FeaturedResultModel.Data>()
    private var playlists : MutableList<PlaylistListResultModel.Data>? = mutableListOf()
    private var llm = LinearLayoutManager(view.context())
    private var trendingAdapter : HomeContentAdapter? = null
    private var playlistAdapter : PlaylistAdapter? = null
    private var playlistLLM = GridLayoutManager(
        view.context(), 4,
        GridLayoutManager.VERTICAL,
        false)
    private var adSliderContent = mutableListOf<AdsModel.Data.Assets>()
    private var adSliderAdapter: AdSliderAdapter? = null

    private var currentPage = 0
    private var handler: Handler = Handler()
    private var update: Runnable? = null
    private var timer: Timer? = null

    //MilliSeconds
    private val DELAY_MS: Long = 1300
    private val PERIOD_MS: Long = 2800

    fun hideDrawerIcon() {
        getToolbarDrawerIcon!!.visibility = android.view.View.GONE
    }

    fun setToolbarSettings(){
        getToolbarText?.text = ""
        getToolbarDrawerIcon?.visibility = android.view.View.VISIBLE
        getToolbarBack?.visibility = android.view.View.GONE
        getToolbarLogo?.visibility = android.view.View.VISIBLE
        getToolbarDrawerIcon?.setOnClickListener{(view.context as DashboardActivity).openDrawer(
            view.getDrawerLayout()
        )}
    }

    fun displayTrending(){
        trendingAdapter = HomeContentAdapter(view.context(), posts!!)
        view.getTrendingRecycler().layoutManager = llm
        view.getTrendingRecycler().adapter = trendingAdapter
    }

    fun displayPlaylist(){
        getPlaylistData()
        playlistAdapter = PlaylistAdapter(view.context(), playlists!!)
        view.getPlaylistRecycler().layoutManager = playlistLLM
        view.getPlaylistRecycler().adapter = playlistAdapter
    }

    fun getPlaylistData() {

        getRepositories?.getPlaylistList(
            getSessionManager?.getToken()!!,
            object : RetrofitService.ResultHandler<PlaylistListResultModel> {
                override fun onSuccess(data: PlaylistListResultModel?) {

                    try {
                        playlists?.clear()
                        data?.data?.forEach { data ->
                            if (playlists?.size != 3) {
                                playlists?.add(data)
                            }
                        }
                        if(!checkRestrictionSilent(Constants.LIST_PLAYLIST)) {
                            playlists?.clear()
                            checkPlayListCount(8, 0)
                        }else{
                            checkPlayListCount(0, 8)
                        }
                        playlistAdapter?.notifyDataSetChanged()
                        println(Gson().toJson(playlists))

                        view.getSwipeRefresh().setRefreshing(false)
                        view.stopShimmerAnimPlayList()
                    } catch (e: Exception) {
                    }
                }

                override fun onError(error: PlaylistListResultModel?) {
                    Log.d("getPlaylistDataERR", error?.message!!)
                    checkPlayListCount(8, 0)
                    view.getSwipeRefresh().setRefreshing(false)
                    view.stopShimmerAnimPlayList()
                }

                override fun onFailed(message: String) {
                    Log.d("getPlaylistDataFailed", message)
                    checkPlayListCount(8, 0)
                    view.getSwipeRefresh().setRefreshing(false)
                    view.stopShimmerAnimPlayList()
                    view.noDataToShow()
                }
            })
    }

    fun checkPlayListCount(visibilityRecycler: Int, visibilityCard: Int){
        view.getPlaylistRecycler().visibility = visibilityRecycler
        view.getCardView().visibility = visibilityCard
//        view.getPlaylistCreate().visibility = visibilityRecycler
    }

    fun getContentId(position: Int) : String {
        return featured[position].featured_content_id.toString()
    }

    fun displayFeaturedPrograms(){
        getRepositories?.getFeaturedPrograms(
            getSessionManager?.getToken()!!,
            object : RetrofitService.ResultHandler<FeaturedProgramsResultModel> {
                override fun onSuccess(data: FeaturedProgramsResultModel?) {
                    try {
                        view.stopShimmerAnimTrendsList()
                        posts.clear()
                        posts.addAll(data?.data!!)
                        displayTrending()
                        view.getSwipeRefresh().setRefreshing(false)
                        Log.d("Featured_Data", Gson().toJson(posts))
                    } catch (e: Exception) {
                    }
                }

                override fun onError(error: FeaturedProgramsResultModel?) {
                    view.stopShimmerAnimTrendsList()
                    view.getSwipeRefresh().setRefreshing(false)
                    Log.d("getFeaturedStationError", error?.message!!)
                }

                override fun onFailed(message: String) {
                    view.stopShimmerAnimTrendsList()
                    view.getSwipeRefresh().setRefreshing(false)
                    Log.d("getFeaturedsSFailed", message)
                    view.noDataToShow()
                }
            })
    }
    fun displayFeatured(){
        getRepositories?.featuredStation(
        getSessionManager?.getToken()!!,
        object : RetrofitService.ResultHandler<FeaturedResultModel> {
            override fun onSuccess(data: FeaturedResultModel?) {
                try {
                    view.stopShimmerFeaturedContent()
                    featured.clear()
                    featured.addAll(data?.data!!)
                    updateFeatured()
                } catch (e: Exception) { }
            }

            override fun onError(error: FeaturedResultModel?) {
                Log.d("displayFeaturedERR", error?.message!!)
                view.stopShimmerFeaturedContent()
            }

            override fun onFailed(message: String) {
                Log.d("displayFeaturedFailed", message)
                view.stopShimmerFeaturedContent()
                view.noDataToShow()
            }
        })
    }

    fun createPlaylist() {
        if(getSessionManager?.isGuest() == true) {
            view.context().guestWarningDialog()
            return
        }
        val dialog = Dialog(view.context(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_create_playlist)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val cancelCreatePlaylist = dialog.findViewById<ImageView>(R.id.cancelCreatePlaylist)
        val playlistName = dialog.findViewById<EditText>(R.id.playlistName)
        val createBtn = dialog.findViewById<TextView>(R.id.createBtn)

        cancelCreatePlaylist.setOnClickListener{dialog.dismiss()}
        createBtn.setOnClickListener{
            if (playlistName.text.isNullOrEmpty()) view.context?.toast("Playlist name is empty!")
            else {
                playlistCreate(playlistName.text.toString())
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun playlistCreate(playlistName: String) {
        getRepositories?.createPlaylist(
            playlistName,
            getSessionManager?.getToken()!!,
            object : RetrofitService.ResultHandler<GeneralResultModel> {
                override fun onSuccess(data: GeneralResultModel?) {
                    getPlaylistData()
                    Toast.makeText(
                        view.context(),
                        "Playlist successfully created!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(error: GeneralResultModel?) {
                    Toast.makeText(
                        view.context(),
                        "Something went wrong, Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailed(message: String) {
                    Toast.makeText(
                        view.context(),
                        "Server Error, Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    @SuppressLint("WrongConstant")
    private fun updateFeatured() {
        try {
            val viewList: List<ImageView> =
                listOf(view.getFeaturedOne(), view.getFeaturedTwo(), view.getFeaturedThree())

            val containerList: List<ConstraintLayout> =
                listOf(view.getFeaturedOneCon(), view.getFeaturedTwoCon(), view.getFeaturedThreeCon())
            var index = 0
            featured.forEach {
                glide(it.logo, viewList[index], containerList[index])
                index++
            }
        } catch (e: Exception) {}
    }

    @SuppressLint("WrongConstant")
    private fun glide(url: String?, image: ImageView, container: ConstraintLayout){
        try {
            val picture = url ?: ""
            Glide.with(view.context())
                .load(picture)
                .placeholder(R.drawable.ic_no_image_2)
                .into(image)
            container.visibility = 0
        } catch (e: Exception){}
    }

    fun getAdRequest(): AdRequest? {
        MobileAds.initialize(view.context())
        return AdRequest.Builder().build()
    }

    fun getStationId(i: Int): Int? {
        return featured[i].id
    }

    fun getStationName(i: Int): String? {
        return featured[i].name
    }

    fun getStationType(i: Int): String? {
        return featured[i].type
    }

    fun stationToggle(fragmentManagers: FragmentManager?, stationFragment: FeaturedFragment, i: Int) {
        if(featured.size < i+1){
            Toast.makeText(view.context(), "No Content", Toast.LENGTH_SHORT).show()
            return
        }
        when(i){
            0 -> (stationFragment as AbstractInterface.DataHandler<Int?>).passData(getStationId(0))
            1 -> (stationFragment as AbstractInterface.DataHandler<Int?>).passData(getStationId(1))
            2 -> (stationFragment as AbstractInterface.DataHandler<Int?>).passData(getStationId(2))
        }
        Services.changeFragment(fragmentManagers!!, stationFragment, "StationsFragment")
    }

    fun featuredToggle(
        context: Context?, i: Int
    ) {
        val channelFragment = ChannelFragment()
        if(featured.size < i+1){
            return
        }
//
//        if (featured[i].type.toString() == "radio" || featured[i].type.toString() == "audio") {
//            if (getContentId(i) == "null") {
//                openFragmentPage(fragmentManagers,
//                    FeaturedStreamFragment(), "FeaturedStreamFragment", i, featured[i].type.toString())
//            } else {
//                openFragmentPage(fragmentManagers,
//                    AudioStreamFragment(), "AudioStreamFragment", i, featured[i].type.toString())
//            }
//        }  else {
//            openFragmentPage(fragmentManagers,
//                FeaturedStreamFragment(), "FeaturedStreamFragment", i, featured[i].type.toString())
//        }
        Services.changeFragment(
            (context as FragmentActivity).supportFragmentManager,
            channelFragment,
            "ChannelFragment"
        )
        (channelFragment as StationDetailsHandler).stationDetails(featured[i].id!!)
        (view.context as DashboardActivity).setBottomNav(R.id.station)
    }

    private fun openFragmentPage(
        fragmentManagers: FragmentManager?, fragment: Fragment,
        tagName: String, i: Int, stationType: String
    ){
        Services.changeFragment(
            fragmentManagers!!,
            fragment,
            tagName,
            getStationId(i).toString(),
            getContentId(i),
            featured[i].name.toString(),
            stationType
        )
    }

    fun displayAdSlider() {
        adSliderAdapter = AdSliderAdapter(view.context(), adSliderContent)
        view.getViewPager().adapter = adSliderAdapter
        view.getIndicator().setViewPager2(view.getViewPager())
        adSliderContent.clear()
        getRepositories?.getAds(getSessionManager?.getToken()!!,
            "homepage",
            object : RetrofitService.ResultHandler<AdsModel> {
                override fun onSuccess(data: AdsModel?) {
                    println("getAds: ${Gson().toJson(data)}")
                    data?.data?.forEach { ads ->
                        if (ads.active!!) {
                            if (ads.section == "homepage" && ads.type == "slider") {
                                adSliderContent.addAll(ads.assets)
                            }
                        }
                    }
                    adSliderAdapter?.notifyDataSetChanged()
                    when (adSliderContent.size) {
                        0 -> view.getLayoutSlider().visibility = android.view.View.GONE
                        else -> {
                            view.getLayoutSlider().visibility = android.view.View.VISIBLE
                            initSwipeSliderTask(adSliderContent.size)
                        }
                    }
                }

                override fun onError(error: AdsModel?) {
                    println("getAdsError: ${Gson().toJson(error)}")
                    view.getLayoutSlider().visibility = android.view.View.GONE
                }

                override fun onFailed(message: String) {
                    println("getAdsFailed: $message")
                    view.getLayoutSlider().visibility = android.view.View.GONE
                }
            })
    }

    private fun initSwipeSliderTask(totalPage: Int) = try {
        handler = Handler()
        //Remove the callbacks of the handler every initialization (removeCallbacks(update!!))
        if (update != null) handler.removeCallbacks(update!!)
        update = Runnable {
            if (currentPage == totalPage - 1) {
                currentPage = 0
            } else {
                currentPage++
            }
            view.getViewPager().setCurrentItem(currentPage, true)
        }

        //Creating new thread instance.
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update!!)
            }
        }, DELAY_MS, PERIOD_MS)
    } catch (e: Exception) {}


    fun deleteFavorite(contentId: String){
        getRepositories?.deleteFavorite(
            contentId,
            getSessionManager?.getToken()!!,
            object: RetrofitService.ResultHandler<GeneralResultModel>{
                override fun onSuccess(data: GeneralResultModel?) {
                    view.context?.toast("${data?.message}")
                    displayFeaturedPrograms()
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

    fun addToFavorites(contentId: String) {
        getRepositories?.addToFavorites(
            contentId,
            getSessionManager?.getToken()!!,
            object: RetrofitService.ResultHandler<GeneralResultModel>{
                override fun onSuccess(data: GeneralResultModel?) {
                    view.context?.toast("${data?.message}")
                    displayFeaturedPrograms()
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

    fun checkRestriction(buttonId: Int): Boolean {
        if (getSessionManager?.isGuest() == true){
            view.context().guestWarningDialog()
            return false
        }
        when(buttonId){
            R.id.cardCreatePlayList -> createPlaylist()
            R.id.playlistBtn -> Services.changeFragment(view.fragmentManagers!!, PlaylistFragment(),"PlaylistFragment")
        }
        return true
    }

    fun checkRestrictionSilent(buttonId: Int): Boolean {
        if (getSessionManager?.isGuest() == true){
            return false
        }
        when(buttonId){

        }
        return true
    }



    interface View : AbstractView{
        fun getTrendingRecycler() : RecyclerView
        fun getPlaylistRecycler() : RecyclerView
        fun getPlaylistCreate() : ViewGroup
        fun getCardView() : CardView
        fun getFeaturedOne() : CircularImageView
        fun getFeaturedTwo() : CircularImageView
        fun getFeaturedThree() : CircularImageView
        fun getDrawerLayout() : DrawerLayout
        fun getSwipeRefresh() : PullRefreshLayout
        fun getLayoutSlider() : RelativeLayout
        fun noDataToShow()
        fun stopShimmerAnimPlayList()
        fun stopShimmerAnimTrendsList()
        fun stopShimmerFeaturedContent()
        fun setFragmentDetails(fragment: Fragment, position: Int, tag: String)
        fun getViewPager(): ViewPager2
        fun getIndicator(): SpringDotsIndicator

        fun getFeaturedOneCon() : ConstraintLayout
        fun getFeaturedTwoCon(): ConstraintLayout
        fun getFeaturedThreeCon(): ConstraintLayout
    }

    interface CreatePlaylist{
       fun onAdapterCreatePlaylistClick()
    }
}