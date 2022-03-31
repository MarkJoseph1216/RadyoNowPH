package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.content

import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.GeneralResultModel
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistContentsResultModel
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistListResultModel
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.utils.guestWarningDialog
import io.supercharge.shimmerlayout.ShimmerLayout

class PlaylistContentPresenter(var view: PlaylistContentFragment): AbstractPresenter<PlaylistContentFragment>(
    view) {

    private var playlistContents : MutableList<PlaylistContentsResultModel.Data> = mutableListOf()
    private var adapter = PlaylistContentAdapter(view.context(), playlistContents)
    private var LLM = LinearLayoutManager(view.context())
    private var stationDetails : PlaylistListResultModel.Data? = null

    init {
        bindStationDetails()
        displayContent()
        updateContentData()
        setToolbarSetup()
    }

    fun startShimmerLayout(){
        view.getRecycler().visibility = android.view.View.GONE
        view.getShimmerLayout().visibility = android.view.View.VISIBLE
        view.getShimmerLayout().startShimmerAnimation()
    }

    fun stopShimmerLayout(){
        view.getShimmerLayout().stopShimmerAnimation()
        view.getRecycler().visibility = android.view.View.VISIBLE
        view.getShimmerLayout().visibility = android.view.View.GONE
    }

    fun displayErrorMessage(){
        view.getNoContent().visibility = android.view.View.VISIBLE
        view.getRecycler().visibility = android.view.View.GONE
        view.getShimmerLayout().visibility = android.view.View.GONE
    }

    private fun displayContent(){
        startShimmerLayout()
        view.getRecycler().adapter = adapter
        view.getRecycler().layoutManager = LLM
    }

    private fun bindStationDetails(){
        stationDetails = view.getStationData()
    }

    private fun setToolbarSetup(){
        //Support all versions used
//        val typeface = ResourcesCompat.getFont(view.context(), font.metropolis_medium)
        getToolbarDrawerIcon!!.visibility = android.view.View.GONE
        getToolbarBack!!.visibility = android.view.View.VISIBLE
        getToolbarLogo!!.visibility = android.view.View.GONE
//        getToolbarText?.ellipsize = TextUtils.TruncateAt.END
//        getToolbarText?.maxLines = 2
        getToolbarText?.text = stationDetails?.name
        getToolbarText?.visibility = android.view.View.VISIBLE
//        getToolbarText?.typeface = typeface
    }

    fun updateContentData() {
        getRepositories?.playlistContents(
            stationDetails?.id!!,
            getSessionManager?.getToken()!!,
            object : RetrofitService.ResultHandler<PlaylistContentsResultModel> {
                override fun onSuccess(data: PlaylistContentsResultModel?) {
                    playlistContents.clear()
                    playlistContents.addAll(data?.data!!)
                    adapter.notifyDataSetChanged()
                    stopShimmerLayout()
                }

                override fun onError(error: PlaylistContentsResultModel?) {
                    Log.d("Error", error?.message!!)
                    stopShimmerLayout()
                    displayErrorMessage()
                }

                override fun onFailed(message: String) {
                    Log.d("Error", message)
                    stopShimmerLayout()
                    displayErrorMessage()
                }
            })
    }

    fun checkRestrictions(buttonId: Int): Boolean{
        if (getSessionManager?.isGuest() == true){
            view.context().guestWarningDialog()
            return false
        }
        when(buttonId){ }
        return true
    }

    fun removeContent(contentId: Int) {
        getRepositories?.removePlaylistContent(
            stationDetails?.id!!,
            contentId,
            getSessionManager?.getToken()!!,
            object : RetrofitService.ResultHandler<GeneralResultModel> {
                override fun onSuccess(data: GeneralResultModel?) {
                    Toast.makeText(view.context(), "Content Removed!", Toast.LENGTH_SHORT).show()
                    updateContentData()
                }

                override fun onError(error: GeneralResultModel?) {
                    Log.d("Error", error?.message!!)
                }

                override fun onFailed(message: String) {
                    Log.d("Error", message)
                }
            })
    }

    interface View : AbstractView{
        fun getRecycler() : RecyclerView
        fun getShimmerLayout() : ShimmerLayout
        fun getStationData() : PlaylistListResultModel.Data
        fun getNoContent(): RelativeLayout
    }
}