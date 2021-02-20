package com.radyopilipinomediagroup.radyonow.ui.dashboard.playlist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyonow.R
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter


class PlaylistFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>, PlaylistPresenter.View{

    private var albumRecycler : RecyclerView? = null
    private var itemsRecycler : RecyclerView? = null
    private var playlistView : View? = null
    private var presenter : PlaylistPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        playlistView = inflater.inflate(R.layout.fragment_playlist, container, false)
        initInitialize()
        initMain()
        return playlistView
    }

    private fun initInitialize() {
        albumRecycler = playlistView?.findViewById(R.id.albumRecycler)
        itemsRecycler = playlistView?.findViewById(R.id.itemsRecycler)
        presenter = PlaylistPresenter(this)
    }

    private fun initMain() {
        presenter?.displayAlbums()
        presenter?.displayItems()
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

    override fun getItemsRecycler(): RecyclerView {
       return itemsRecycler!!
    }

    override fun getAlbumRecycler(): RecyclerView {
        return albumRecycler!!
    }

}