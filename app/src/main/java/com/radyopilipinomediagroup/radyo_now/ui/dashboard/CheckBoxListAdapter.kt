package com.radyopilipinomediagroup.radyo_now.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistListResultModel

class CheckBoxListAdapter(var context: Context,var playlists: List<PlaylistListResultModel.Data>) :
    RecyclerView.Adapter<CheckBoxListAdapter.ViewHolder>() {
    var playlistsId : MutableList<Int>? = mutableListOf()
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var playlist : CheckBox = itemView.findViewById(R.id.playlist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_view_playlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val playlist = playlists[position]
        holder.playlist.text = playlist.name
        holder.playlist.setOnCheckedChangeListener { view, isChecked ->
            if(isChecked) addId(playlists[position].id!!)
            else removeID(playlists[position].id!!)
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun addId(id: Int){
        playlistsId?.plusAssign(id)
    }
    fun removeID(id: Int){
        playlistsId?.removeAll(listOf(id))
    }

    fun getPlaylistId() : MutableList<Int>?{
        return playlistsId
    }

}
