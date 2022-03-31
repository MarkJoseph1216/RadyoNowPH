package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.AlbumModel
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.streams.AudioStreamFragment
import com.radyopilipinomediagroup.radyo_now.utils.Services

class AlbumAdapter(var context : Context, var albums : List<AlbumModel>) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var albumThumb : ImageView = itemView.findViewById(R.id.albumThumb)
        var albumTitle : TextView = itemView.findViewById(R.id.albumTitle)
        var albumSongNumber : TextView = itemView.findViewById(R.id.albumSongNumber)
        var albumRating : TextView = itemView.findViewById(R.id.albumRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_album,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = albums[position]
        initComponents(holder, album)
        initOnClickListener(holder)
    }

    private fun initComponents(holder: ViewHolder, albums: AlbumModel){
        Glide.with(context)
            .load(albums.albumThumb)
            .centerCrop()
            .placeholder(R.drawable.ic_no_image)
            .into(holder.albumThumb)
        holder.albumTitle.text = albums.albumTitle
        holder.albumSongNumber.text = albums.albumSongNumber
        holder.albumRating.text = albums.albumRating
    }

    private fun initOnClickListener(holder: ViewHolder){
        holder.itemView.setOnClickListener {
            Services.changeFragment(
                (context as FragmentActivity).supportFragmentManager,
                AudioStreamFragment(),
                "AudioStreamFragment"
            )
        }
    }

    override fun getItemCount(): Int {
        return albums.size
    }
}