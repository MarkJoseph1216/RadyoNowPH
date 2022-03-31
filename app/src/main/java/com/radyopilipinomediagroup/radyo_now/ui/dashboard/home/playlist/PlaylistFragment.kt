package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.AbstractInterface
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import io.supercharge.shimmerlayout.ShimmerLayout


class PlaylistFragment : Fragment(),View.OnClickListener, AbstractPresenter.ContextView<FragmentActivity>,
    PlaylistPresenter.View, AbstractInterface.PlaylistHandler, AbstractInterface.PlaylistSettings{

    private var itemsRecycler : RecyclerView? = null
    private var itemsHeaderRecycler : RecyclerView? = null
    private var playlistView : View? = null
    private var presenter : PlaylistPresenter? = null
    private var shimmerLayoutAllPlayList : ShimmerLayout? = null
    private var playlistCreate : ViewGroup? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        playlistView = inflater.inflate(R.layout.fragment_playlist, container, false)
        initInitialize()
        initComponents()
        return playlistView
    }

    private fun initInitialize() {
        playlistCreate = playlistView?.findViewById(R.id.playlistCreate)
        itemsRecycler = playlistView?.findViewById(R.id.itemsRecycler)
        itemsHeaderRecycler = playlistView?.findViewById(R.id.itemsHeaderRecycler)
        shimmerLayoutAllPlayList = playlistView?.findViewById(R.id.shimmerLayoutAllPlayList)
        presenter = PlaylistPresenter(this)
    }

    private fun initComponents() {
        presenter?.setToolbarSetup()
        presenter?.displayItems()
        initListener()
    }

    private fun initListener() {
        playlistCreate?.setOnClickListener(this::onClick)
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

    override fun getItemsHeaderRecycler(): RecyclerView {
        return itemsHeaderRecycler!!
    }

    override fun getShimmerLayout(): ShimmerLayout {
        return shimmerLayoutAllPlayList!!
    }

    override fun updatePlaylistData() {
        presenter?.getPlaylist()
    }

    override fun onRenamePlaylist(playlistId: Int, oldName: String) {
        presenter?.renamePlaylist(playlistId, oldName)
    }

    override fun onDeletePlaylist(playlistId: Int) {
        presenter?.deletePlaylist(playlistId)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.playlistCreate -> presenter?.createPlaylist()
        }
    }
}