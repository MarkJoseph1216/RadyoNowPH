package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.GeneralResultModel
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistListResultModel
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.utils.toast
import io.supercharge.shimmerlayout.ShimmerLayout

class PlaylistPresenter(var view : PlaylistFragment): AbstractPresenter<PlaylistFragment>(view) {

    private var streamItems :  MutableList<PlaylistListResultModel.Data>? = mutableListOf()
//    private var featuredItems :  MutableList<PlaylistListResultModel.Data>? = mutableListOf()
//    private var itemsHeaderAdapter = PlaylistProgramAdapter(view.context(), featuredItems!!, 0)
    private var itemsAdapter = PlaylistProgramAdapter(view.context(), streamItems!!, 1)
    private var llmHeader = LinearLayoutManager(view.context(), LinearLayoutManager.HORIZONTAL, false)
    private var llm = LinearLayoutManager(view.context())
    private var renameDialog : Dialog? = null
    fun startShimmerLayout(){
        view.getItemsRecycler().visibility = android.view.View.GONE
        view.getItemsHeaderRecycler().visibility = android.view.View.GONE
        view.getShimmerLayout().visibility = android.view.View.VISIBLE
        view.getShimmerLayout().startShimmerAnimation()
    }

    fun stopShimmerLayout(){
        view.getShimmerLayout().stopShimmerAnimation()
        view.getItemsRecycler().visibility = android.view.View.VISIBLE
        view.getItemsHeaderRecycler().visibility = android.view.View.VISIBLE
        view.getShimmerLayout().visibility = android.view.View.GONE
    }

    fun displayItems(){
        startShimmerLayout()
        getPlaylist()

        view.getItemsRecycler().layoutManager = llm
        view.getItemsRecycler().adapter = itemsAdapter

        view.getItemsHeaderRecycler().layoutManager = llmHeader
//        view.getItemsHeaderRecycler().adapter = itemsHeaderAdapter
    }

    fun getPlaylist(){
        getRepositories?.getPlaylistList(getSessionManager?.getToken()!!, object:RetrofitService.ResultHandler<PlaylistListResultModel>{
            override fun onSuccess(data: PlaylistListResultModel?) {
                streamItems?.clear()
//                featuredItems?.clear()
//                if (data?.data?.size!! <= 2) featuredItems?.addAll(data.data)
//                else{
                    for(i in data?.data?.indices!!){
//                        if(i < 2) featuredItems?.add(data?.data[i])
//                        else
                            streamItems?.add(data.data[i])
                    }
//                }
                updateList()
                stopShimmerLayout()
            }
            override fun onError(error: PlaylistListResultModel?) = onFailedTask(error?.message)
            override fun onFailed(message: String) = onFailedTask(message)
        })
    }

    private fun onFailedTask(errorMsg: String?){
        Log.d("GetError: ", errorMsg.toString())
        streamItems?.clear()
        updateList()
        stopShimmerLayout()
    }

    private fun updateList() {
        itemsAdapter.notifyDataSetChanged()
//        itemsHeaderAdapter.notifyDataSetChanged()
    }

    fun setToolbarSetup(){
        getToolbarDrawerIcon!!.visibility = android.view.View.GONE
        getToolbarBack!!.visibility = android.view.View.VISIBLE
        getToolbarText?.text = "Playlists"
        getToolbarLogo!!.visibility = android.view.View.VISIBLE
    }

    fun renamePlaylist(playlistId: Int, old: String) {
        renameDialog = Dialog(view.context(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        renameDialog?.setContentView(R.layout.dialog_rename_playlist)
        renameDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val oldName : TextView = renameDialog?.findViewById(R.id.oldName)!!
        val newName : EditText = renameDialog?.findViewById(R.id.newName)!!
        val renameBtn: TextView = renameDialog?.findViewById(R.id.renameBtn)!!
        val background : RelativeLayout = renameDialog?.findViewById(R.id.background)!!
        oldName.text = old
        background.setOnClickListener { renameDialog?.dismiss() }
        renameBtn.setOnClickListener {
            if(newName.text.toString().isEmpty() || newName.text.toString().isBlank()) view.context?.toast("Name field is empty.")
            else {
                doRename(playlistId, newName.text.toString())
            }
        }
        renameDialog?.show()
    }

    private fun doRename(playlistId: Int, toString: String) {
        getRepositories?.renamePlaylist(playlistId,toString,getSessionManager?.getToken()!!,object : RetrofitService.ResultHandler<GeneralResultModel>{
            override fun onSuccess(data: GeneralResultModel?) {
                getPlaylist()
                view.context().toast("Success!")
                renameDialog?.dismiss()
            }
            override fun onError(error: GeneralResultModel?) {
                view.context().toast("${error?.message}")
            }
            override fun onFailed(message: String) {
                println("doRename_FAILED: $message")
            }
        })
    }

    fun deletePlaylist(playlistId: Int) {
        getRepositories?.deletePlaylist(playlistId, getSessionManager?.getToken()!!, object : RetrofitService.ResultHandler<GeneralResultModel> {
            override fun onSuccess(data: GeneralResultModel?) {
                Toast.makeText(view.context(), "Playlist has been deleted", Toast.LENGTH_SHORT).show()
                view.updatePlaylistData()
            }
            override fun onError(error: GeneralResultModel?) {
                Log.d("Error", error?.message!!)
            }
            override fun onFailed(message: String) {
                Log.d("Error", message)
            }
        })
    }

    fun createPlaylist() {
        (view.context as DashboardActivity).onCreatePlaylist()
    }

    interface View : AbstractView{
        fun getItemsRecycler() : RecyclerView
        fun getItemsHeaderRecycler() : RecyclerView
        fun getShimmerLayout() : ShimmerLayout
    }
}