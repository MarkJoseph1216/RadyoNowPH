package com.radyopilipinomediagroup.radyonow.ui.dashboard.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyonow.R
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyonow.ui.dashboard.DashboardActivity

class HomeFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>, HomePresenter.View {

    private var circleHead: RecyclerView? = null
    private var trending: RecyclerView? = null
    private var trendingContainer: LinearLayout? = null
    private var presenter: HomePresenter? = null
    private var homeView : View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeView = inflater.inflate(R.layout.fragment_home, container, false)
        initInitialize()
        initMain()
        return homeView
    }

    private fun initInitialize() {
        circleHead = homeView?.findViewById(R.id.circleHead)
        trending = homeView?.findViewById(R.id.trending)
        trendingContainer = homeView?.findViewById(R.id.trendingContainer)
        presenter = HomePresenter(this)
    }

    private fun initMain() {
        presenter?.displayCircleHead()
        presenter?.displayTrending()
    }


    override fun getFeaturedRecycler(): RecyclerView {
        return circleHead!!
    }

    override fun getTrendingRecycler(): RecyclerView {
        return trending!!
    }

    override fun getFeaturedContainer(): LinearLayout {
        return trendingContainer!!
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

}