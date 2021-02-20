package com.radyopilipinomediagroup.radyonow.ui.dashboard.playlist

import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyonow.ui.StreamListAdapter

class PlaylistPresenter(var view : PlaylistFragment): AbstractPresenter<PlaylistFragment>(view) {

    var streamItems = getRepositories?.getFavorites()
    var albumList = getRepositories?.getAlbum()
    var itemsAdapter = StreamListAdapter(view.context(), streamItems!!)
    var albumAdapter = AlbumAdapter(view.context(), albumList!!)
    var llm = LinearLayoutManager(view.context())
    var llmHorizontal = LinearLayoutManager(view.context(), LinearLayoutManager.HORIZONTAL,false)

    fun displayItems(){
        view.getItemsRecycler().layoutManager = llm
        view.getItemsRecycler().adapter = itemsAdapter
    }
    fun displayAlbums(){
        view.getAlbumRecycler().layoutManager = llmHorizontal
        view.getAlbumRecycler().adapter = albumAdapter
    }

    interface View : AbstractPresenter.AbstractView{
        fun getItemsRecycler() : RecyclerView
        fun getAlbumRecycler() : RecyclerView
    }
}