package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home

import android.content.Context
import com.radyopilipinomediagroup.radyo_now.R
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.baoyz.widget.PullRefreshLayout
import com.google.android.gms.ads.*
import com.mikhaellopez.circularimageview.CircularImageView
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.ui.AbstractInterface
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.favorites.FavoritesPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.featured.FeaturedFragment
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import io.supercharge.shimmerlayout.ShimmerLayout


class HomeFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>, HomePresenter.View, View.OnClickListener,
    AbstractInterface.PlaylistHandler, HomePresenter.CreatePlaylist,AbstractInterface.AdsInterface, FavoritesPresenter.FavoriteCallback  {

    private var trendingRecycler: RecyclerView? = null
    private var playlistRecycler: RecyclerView? = null
    private var presenter: HomePresenter? = null
    private var homeView : View? = null
    private var stationOne: ImageView? = null
    private var stationTwo: ImageView? = null
    private var stationThree: ImageView? = null
    private var featuredOne: CircularImageView? = null
    private var featuredTwo: CircularImageView? = null
    private var featuredThree: CircularImageView? = null
    private var cardCreatePlayList: CardView? = null
    var fragmentManagers: FragmentManager? = null
    private var shimmerLayoutTrendList : ShimmerLayout? = null
    private var shimmerFeaturedContent : ShimmerLayout? = null
    private var shimmerLayoutPlayList : ShimmerLayout? = null
    private var swipeRefreshLayout : PullRefreshLayout? = null
    private var playlistBtn: ViewGroup? = null
    private var playlistIcon: ImageView? = null
    private var layoutSliderAds : RelativeLayout? = null
    private var featuredContainer : LinearLayout? = null
    private var playlistContainer : LinearLayout? = null

    private var featuredOneContainer : ConstraintLayout? = null
    private var featuredTwoContainer : ConstraintLayout? = null
    private var featuredThreeContainer : ConstraintLayout? = null

    private var noInternet : TextView? = null
    private var playlistCreate : ViewGroup? = null
    private var adView : AdView? = null
    private var viewPager: ViewPager2? = null
    private var sliderIndicator: SpringDotsIndicator? = null
    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeView = inflater.inflate(R.layout.fragment_home, container, false)
        initInitialize()
        initMain()
        initListener()
        return homeView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu

    }

    private fun initInitialize() {
        featuredOneContainer = homeView?.findViewById(R.id.featuredOneContainer)
        featuredTwoContainer = homeView?.findViewById(R.id.featuredTwoContainer)
        featuredThreeContainer = homeView?.findViewById(R.id.featuredThreeContainer)

        viewPager = homeView?.findViewById(R.id.viewPager)
        adView = homeView?.findViewById(R.id.adView)
        playlistCreate = homeView?.findViewById(R.id.playlistCreate)
        noInternet = homeView?.findViewById(R.id.noInternet)
        playlistContainer = homeView?.findViewById(R.id.playlistContainer)
        shimmerFeaturedContent  = homeView?.findViewById(R.id.shimmerFeaturedContent)
        featuredContainer  = homeView?.findViewById(R.id.featuredContainer)
        playlistBtn = homeView?.findViewById(R.id.playlistBtn)
        playlistIcon = homeView?.findViewById(R.id.playlistIcon)
        stationOne = homeView?.findViewById(R.id.stationOne)
        stationTwo = homeView?.findViewById(R.id.stationTwo)
        stationThree = homeView?.findViewById(R.id.stationThree)
        featuredOne = homeView?.findViewById(R.id.featuredOne)
        featuredTwo = homeView?.findViewById(R.id.featuredTwo)
        featuredThree = homeView?.findViewById(R.id.featuredThree)
        layoutSliderAds = homeView?.findViewById(R.id.layoutSliderAds)
        shimmerLayoutTrendList = homeView?.findViewById(R.id.shimmerLayoutTrendList)
        shimmerLayoutPlayList = homeView?.findViewById(R.id.shimmerLayoutPlayList)
        swipeRefreshLayout = homeView?.findViewById(R.id.swipeRefreshLayout)
        cardCreatePlayList = homeView?.findViewById(R.id.cardCreatePlayList)
        playlistRecycler = homeView?.findViewById(R.id.playlistRecycler)
        trendingRecycler = homeView?.findViewById(R.id.trendingRecycler)
        sliderIndicator = homeView?.findViewById(R.id.sliderIndicator)
        fragmentManagers = activity().supportFragmentManager
        presenter = HomePresenter(this)
    }

    private fun initMain() {
        noInternet?.visibility = View.GONE
        initAdmob()
        presenter?.displayAdSlider()
        presenter?.displayFeatured()
        presenter?.displayFeaturedPrograms()
        presenter?.displayPlaylist()
        presenter?.setToolbarSettings()
        shimmerLayoutTrendList?.startShimmerAnimation()
        shimmerLayoutPlayList?.startShimmerAnimation()
        shimmerFeaturedContent?.startShimmerAnimation()
    }

    private fun initAdmob() {
        MobileAds.initialize(context!!)
        println("initAdmob: Start")
        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
        adView?.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError?) {
                super.onAdFailedToLoad(p0)
                println("initAdmob: ${p0?.message}")
                adView?.visibility = View.GONE
            }
            override fun onAdLoaded() {
                super.onAdLoaded()
                println("initAdmob: Loaded")
                adView?.visibility = View.VISIBLE
            }
        }
    }

    private fun initListener() {
        playlistCreate?.setOnClickListener(this::onClick)
        stationOne?.setOnClickListener(this::onClick)
        stationTwo?.setOnClickListener(this::onClick)
        stationThree?.setOnClickListener(this::onClick)
        featuredOne?.setOnClickListener(this::onClick)
        featuredTwo?.setOnClickListener(this::onClick)
        featuredThree?.setOnClickListener(this::onClick)
        cardCreatePlayList?.setOnClickListener(this::onClick)
        playlistBtn?.setOnClickListener(this::onClick)
        playlistIcon?.setOnClickListener(this::onClick)
        swipeRefreshLayout?.setOnRefreshListener(this::startShimmerLayout)
    }

    private fun startShimmerLayout() {
        adView?.visibility = View.GONE
        layoutSliderAds?.visibility = View.GONE
        cardCreatePlayList?.visibility = View.GONE
        swipeRefreshLayout?.setRefreshing(true)

        shimmerLayoutPlayList?.startShimmerAnimation()
        shimmerLayoutPlayList?.visibility = View.VISIBLE
        playlistContainer?.visibility = View.GONE

        shimmerLayoutTrendList?.startShimmerAnimation()
        shimmerLayoutTrendList?.visibility = View.VISIBLE
        trendingRecycler?.visibility = View.GONE

        shimmerFeaturedContent?.startShimmerAnimation()
        shimmerFeaturedContent?.visibility = View.VISIBLE
        featuredContainer?.visibility = View.INVISIBLE

        noInternet?.visibility = View.GONE
        presenter?.displayFeatured()
        presenter?.displayFeaturedPrograms()
        presenter?.displayPlaylist()
        presenter?.displayAdSlider()
        initAdmob()
    }

    override fun onResume() {
        (activity() as DashboardActivity).homeCountReset()
        super.onResume()
    }

    override fun getTrendingRecycler(): RecyclerView = trendingRecycler!!

    override fun getPlaylistRecycler(): RecyclerView = playlistRecycler!!
    override fun getPlaylistCreate(): ViewGroup = playlistCreate!!
    override fun getCardView(): CardView = cardCreatePlayList!!
    override fun getFeaturedOne(): CircularImageView = featuredOne!!
    override fun getFeaturedTwo(): CircularImageView = featuredTwo!!
    override fun getFeaturedThree(): CircularImageView = featuredThree!!
    override fun getSwipeRefresh(): PullRefreshLayout = swipeRefreshLayout!!
    override fun getLayoutSlider(): RelativeLayout = layoutSliderAds!!

    override fun noDataToShow() {
        playlistContainer?.visibility = View.GONE
        featuredContainer?.visibility = View.GONE
        trendingRecycler?.visibility = View.GONE
        noInternet?.visibility = View.VISIBLE
    }

    override fun getDrawerLayout(): DrawerLayout {
        return activity!!.findViewById(R.id.main_drawer_layout)
    }

    override fun stopShimmerAnimPlayList() {
        shimmerLayoutPlayList?.stopShimmerAnimation()
        shimmerLayoutPlayList?.visibility = View.GONE
        playlistContainer?.visibility = View.VISIBLE
    }

    override fun stopShimmerAnimTrendsList() {
        shimmerLayoutTrendList?.stopShimmerAnimation()
        shimmerLayoutTrendList?.visibility = View.GONE
        trendingRecycler?.visibility = View.VISIBLE
    }

    override fun stopShimmerFeaturedContent() {
        shimmerFeaturedContent?.stopShimmerAnimation()
        shimmerFeaturedContent?.visibility = View.GONE
        featuredContainer?.visibility = View.VISIBLE
    }

    override fun activity(): FragmentActivity = activity!!
    override fun context(): Context = context!!
    override fun applicationContext(): Context = activity().applicationContext

    override fun onClick(v: View?) {
        if (v?.id != R.id.cardCreatePlayList && v?.id != R.id.playlistCreate) presenter?.hideDrawerIcon()
        val stationFragment = FeaturedFragment()
        when(v?.id){
            R.id.stationOne ->  presenter?.stationToggle(fragmentManagers,stationFragment,0)
            R.id.stationTwo ->  presenter?.stationToggle(fragmentManagers,stationFragment,1)
            R.id.stationThree -> presenter?.stationToggle(fragmentManagers,stationFragment,2)
            R.id.featuredOne ->  presenter?.featuredToggle(context(),0)
            R.id.featuredTwo -> presenter?.featuredToggle(context(),1)
            R.id.featuredThree -> presenter?.featuredToggle(context(),2)
            R.id.playlistBtn, R.id.playlistIcon -> presenter?.checkRestriction(R.id.playlistBtn)
            R.id.cardCreatePlayList,
            R.id.playlistCreate -> onAdapterCreatePlaylistClick()
        }
    }

    override fun setFragmentDetails(fragment: Fragment, position: Int, tag: String) {
        Services.changeFragment(
            fragmentManagers!!,
            fragment,
            tag,
            presenter?.getStationId(position).toString(),
            presenter?.getContentId(position).toString(),
            presenter?.getStationName(position).toString(),
            presenter?.getStationType(position).toString()
        )
    }

    override fun getViewPager(): ViewPager2 = viewPager!!
    override fun getIndicator(): SpringDotsIndicator = sliderIndicator!!
    override fun getFeaturedOneCon(): ConstraintLayout = featuredOneContainer!!
    override fun getFeaturedTwoCon(): ConstraintLayout = featuredTwoContainer!!
    override fun getFeaturedThreeCon(): ConstraintLayout = featuredThreeContainer!!

    override fun updatePlaylistData() {
        presenter?.getPlaylistData()
    }

    override fun onAdapterCreatePlaylistClick() {
        presenter?.checkRestriction(R.id.cardCreatePlayList)
    }
    override fun onPopupReady(data: AdsModel.Data) {}
    override fun onSliderReady(data: AdsModel.Data) {}
    override fun onBannerReady(data: AdsModel.Data) {}

    override fun deleteFavorite(contentId: String) {
        presenter?.deleteFavorite(contentId)
    }

    override fun addToFavorites(contentId: String) {
        presenter?.addToFavorites(contentId)
    }

    fun checkRestriction(buttonId: Int): Boolean? = presenter?.checkRestriction(buttonId)
}