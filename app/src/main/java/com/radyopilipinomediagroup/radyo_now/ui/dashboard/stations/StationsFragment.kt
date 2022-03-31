package com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import io.supercharge.shimmerlayout.ShimmerLayout

class StationsFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>, StationsPresenter.View{

    var stationsRecycler : RecyclerView? = null
    var stationView : View? = null
    var presenter : StationsPresenter? = null
    var shimmerLayoutTrendStationsList : ShimmerLayout? = null
    var noInternet : TextView? = null
    var stationsContainer : LinearLayout? = null
    var scrollView : NestedScrollView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        stationView = inflater.inflate(R.layout.fragment_stations2, container, false)
        initInitialize()
        initComponents()
        return stationView
    }

    private fun initInitialize() {
        scrollView = stationView?.findViewById(R.id.scrollView)
        stationsContainer = stationView?.findViewById(R.id.stationsContainer)
        noInternet = stationView?.findViewById(R.id.noInternet)
        stationsRecycler = stationView?.findViewById(R.id.stationsRecycler)
        shimmerLayoutTrendStationsList = stationView?.findViewById(R.id.shimmerLayoutTrendStationsList)
        presenter = StationsPresenter(this)
    }

    private fun initComponents() {
        stateRefresh()
        presenter?.displayStations()
        initListener()
    }

    private fun initListener() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            scrollView?.setOnScrollChangeListener { v: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
                val bottom: Int = scrollView?.getChildAt(0)?.measuredHeight!! - v?.measuredHeight!!
                if (scrollY >= bottom) {
                    if (presenter?.currentPage!! < presenter?.totalPage!!){
                        presenter?.currentPage = presenter?.currentPage!! + 1
                        presenter?.getStation(presenter?.currentPage!!)
                    }
                }
            }
        }
    }

    override fun noDataToShow(){
        noInternet?.visibility = View.VISIBLE
        stationsContainer?.visibility = View.GONE
    }

    override fun stateRefresh(){
        noInternet?.visibility = View.GONE
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

    override fun getStationRecycler(): RecyclerView {
        return stationsRecycler!!
    }

    override fun getShimmerLayout(): ShimmerLayout {
        return shimmerLayoutTrendStationsList!!
    }

}