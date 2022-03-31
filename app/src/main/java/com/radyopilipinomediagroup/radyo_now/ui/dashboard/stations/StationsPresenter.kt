package com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations

import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.model.stations.StationListResultModel
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.utils.log
import io.supercharge.shimmerlayout.ShimmerLayout

class StationsPresenter(var view: StationsFragment): AbstractPresenter<StationsFragment>(view) {

    var slm = GridLayoutManager(view.context,3,GridLayoutManager.VERTICAL,false)
    var stations = mutableListOf<StationListResultModel.Station>()
    val adapter = StationListAdapter(view.context(), stations)
    var totalPage : Int? = 1
    var currentPage: Int? = 1

    init {
        setToolbarSettings()
    }

    private fun setToolbarSettings() {
        getToolbarBack!!.visibility = android.view.View.GONE
        getToolbarDrawerIcon!!.visibility = android.view.View.GONE
        getToolbarLogo!!.visibility = android.view.View.GONE
        getToolbarText!!.text = "Channels"
        getToolbarText!!.visibility = android.view.View.VISIBLE
    }

    fun displayStations() {
        startShimmerLayout()
        getStation(1)
        view.getStationRecycler().adapter = adapter
        view.getStationRecycler().layoutManager = slm

    }

    fun getStation(page: Int) {
        getRepositories?.getStationsList(page,getSessionManager?.getToken()!!, object:RetrofitService.ResultHandler<StationListResultModel>{
            override fun onSuccess(data: StationListResultModel?) {
                stations.addAll(data?.stations!!)
                totalPage = data.meta.pagination.totalPages
                currentPage = data.meta.pagination.currentPage

                adapter.notifyDataSetChanged()
                Log.d("StationsRequest", Gson().toJson(stations))
                stopShimmerLayout()
            }
            override fun onError(error: StationListResultModel?) {
                Toast.makeText(view.context(), "Something went wrong, Please try again.", Toast.LENGTH_SHORT).show()
                stopShimmerLayout()
                view.noDataToShow()
            }
            override fun onFailed(message: String) {
                log(message)
                Toast.makeText(view.context!!, "Server Error, Please try again.", Toast.LENGTH_SHORT).show()
                stopShimmerLayout()
                view.noDataToShow()
            }
        } )
    }

    fun startShimmerLayout() {
        view.stateRefresh()
        getView!!.shimmerLayoutTrendStationsList!!.visibility = android.view.View.VISIBLE
        getView!!.shimmerLayoutTrendStationsList!!.startShimmerAnimation()
        getView!!.getStationRecycler().visibility = android.view.View.GONE
    }

    fun stopShimmerLayout() {
        getView!!.shimmerLayoutTrendStationsList!!.stopShimmerAnimation()
        getView!!.shimmerLayoutTrendStationsList!!.visibility = android.view.View.GONE
        getView!!.getStationRecycler().visibility = android.view.View.VISIBLE
    }

    interface View : AbstractView{
        fun getStationRecycler() : RecyclerView
        fun getShimmerLayout() : ShimmerLayout
        fun noDataToShow()
        fun stateRefresh()
    }
}