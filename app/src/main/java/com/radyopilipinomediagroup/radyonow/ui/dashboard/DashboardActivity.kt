package com.radyopilipinomediagroup.radyonow.ui.dashboard

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyonow.R
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter

class DashboardActivity : AppCompatActivity(), DashboardPresenter.View,
    AbstractPresenter.ContextView<DashboardActivity>,
    AbstractPresenter.AbstractView {

    private var toolbar: Toolbar? = null
    private var circleHead: RecyclerView? = null
    private var trending: RecyclerView? = null
    private var trendingContainer: LinearLayout? = null
    private var presenter: DashboardPresenter? = null

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
        presenter = DashboardPresenter(activity())
    }

    private fun initMain() {
        setSupportActionBar(toolbar)
        actionBar?.title = "Home"
        presenter?.displayCircleHead()
        presenter?.displayTrending()
    }

    override fun activity(): DashboardActivity {
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