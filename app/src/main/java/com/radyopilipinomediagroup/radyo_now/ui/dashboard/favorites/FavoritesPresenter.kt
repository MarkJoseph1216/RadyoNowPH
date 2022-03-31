package com.radyopilipinomediagroup.radyo_now.ui.dashboard.favorites

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.model.GeneralResultModel
import com.radyopilipinomediagroup.radyo_now.model.stations.StationContentsResultModel
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.utils.guestWarningDialog
import com.radyopilipinomediagroup.radyo_now.utils.toast

class FavoritesPresenter(var view : FavoritesFragment) : AbstractPresenter<FavoritesFragment>(view) {

    private var favorites = mutableListOf<StationContentsResultModel.Data>()
    private var adapter = FavoritesListAdapter(view.context(), favorites)
    private var llm = LinearLayoutManager(view.context())

    init {
        displayFavorites()
        getFavoriteList()
        initToolbarSetup()
    }

    @SuppressLint("SetTextI18n")
    private fun initToolbarSetup(){
        getToolbarLogo?.visibility = android.view.View.VISIBLE
        getToolbarDrawerIcon?.visibility = android.view.View.GONE
        getToolbarBack?.visibility = android.view.View.GONE
        getToolbarText?.visibility = android.view.View.VISIBLE
        getToolbarText?.text = "Favorites"
    }

    fun deleteFavorite(contentId: String){
        getRepositories?.deleteFavorite(
            contentId,
            getSessionManager?.getToken()!!,
            object: RetrofitService.ResultHandler<GeneralResultModel>{
                override fun onSuccess(data: GeneralResultModel?) {
                    view.context?.toast("Deleted")
                    getFavoriteList()
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

    private fun getFavoriteList() {
        favorites.clear()
        getRepositories?.getFavoriteList(getSessionManager?.getToken()!!,
        object:RetrofitService.ResultHandler<StationContentsResultModel>{
            override fun onSuccess(data: StationContentsResultModel?) {
                favorites.addAll(data?.data!!)
                adapter.notifyDataSetChanged()

                when(favorites.size) {
                    0 -> view.setNoContentVisible(0, 8)
                    else -> view.setNoContentVisible(8, 8)
                }
            }
            override fun onError(error: StationContentsResultModel?) {
                view.setNoContentVisible(0, 8)
            }
            override fun onFailed(message: String) {
                view.setNoContentVisible(0, 8)
                view.context?.toast("Failed to load, Please try again!")
            }
        })
    }

    fun displayFavorites(){
        view.getRecycler().adapter = adapter
        view.getRecycler().layoutManager = llm
    }

    fun checkRestriction(buttonId: Int): Boolean? {
        if (getSessionManager?.isGuest() == true){
            view.context().guestWarningDialog()
            return false
        }
        when(buttonId){ }
        return true
    }

    interface FavoriteCallback{
        fun deleteFavorite(contentId: String)
        fun addToFavorites(contentId: String)
    }

    interface View : AbstractPresenter.AbstractView{
        fun getRecycler() : RecyclerView
        fun setNoContentVisible(visible: Int, loadingVisible: Int)
    }
}