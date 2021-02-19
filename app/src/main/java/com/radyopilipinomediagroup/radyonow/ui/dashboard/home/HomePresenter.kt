package com.radyopilipinomediagroup.radyonow.ui.dashboard


import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter

class DashboardPresenter(val view : DashboardActivity): AbstractPresenter<DashboardActivity>(view) {

    private var posts = getRepositories?.getTrending()
    private var featured = getRepositories?.getFeatured()
    private var llmHorizontal = LinearLayoutManager(view.context(),LinearLayoutManager.HORIZONTAL,false)
    private var llm = LinearLayoutManager(view.context())
    private var circleHeadAdapter = CircleHeadAdapter(view.context(), featured!!)
    private var trendingAdapter = TrendingAdapter(view.context(), posts!!)

    fun displayCircleHead(){
        if(featured?.size != 0){
            view.getFeaturedRecycler()?.layoutManager = llmHorizontal
            view.getFeaturedRecycler()?.adapter = circleHeadAdapter
        }else{
            view.getFeaturedContainer()?.visibility = android.view.View.GONE
        }
    }

    fun displayTrending(){
        view.getTrendingRecycler()?.layoutManager = llm
        view.getTrendingRecycler()?.adapter = trendingAdapter
    }

    interface View : AbstractPresenter.AbstractView{
        fun getFeaturedRecycler() : RecyclerView?
        fun getTrendingRecycler() : RecyclerView?
        fun getFeaturedContainer() : LinearLayout?
    }
}