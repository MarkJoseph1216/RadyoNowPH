package com.radyopilipinomediagroup.radyonow.ui.dashboard.home

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyonow.R
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter

class HomeActivity : AppCompatActivity(), HomePresenter.View,
    AbstractPresenter.ContextView<HomeActivity>,
    AbstractPresenter.AbstractView {

    private var toolbar: Toolbar? = null
    private var circleHead: RecyclerView? = null
    private var trending: RecyclerView? = null
    private var trendingContainer: LinearLayout? = null
    private var presenter: HomePresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        initDeclaration();
        initMain();
    }

    private fun initDeclaration() {
        toolbar = findViewById(R.id.toolbar)
        circleHead = findViewById(R.id.circleHead)
        trending = findViewById(R.id.trending)
        trendingContainer = findViewById(R.id.trendingContainer)
        presenter = HomePresenter(activity())
    }

    private fun initMain() {
        setSupportActionBar(toolbar)
        actionBar?.title = "Home"
        presenter?.displayCircleHead()
        presenter?.displayTrending()
    }

    override fun activity(): HomeActivity {
        return this
    }

    override fun context(): Context {
        return this
    }

    override fun applicationContext(): Context {
        return applicationContext
    }

    override fun getFeaturedRecycler(): RecyclerView? {
        return circleHead
    }

    override fun getTrendingRecycler(): RecyclerView? {
        return trending
    }

    override fun getFeaturedContainer(): LinearLayout? {
        return trendingContainer
    }

}