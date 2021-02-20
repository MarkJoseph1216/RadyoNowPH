package com.radyopilipinomediagroup.radyonow.ui.dashboard.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.radyopilipinomediagroup.radyonow.R
import com.radyopilipinomediagroup.radyonow.model.AlbumModel

class AlbumAdapter(var context : Context, var albums : List<AlbumModel>) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var albumThumb : ImageView = itemView.findViewById(R.id.albumThumb)
        var albumTitle : TextView = itemView.findViewById(R.id.albumTitle)
        var albumSongNumber : TextView = itemView.findViewById(R.id.albumSongNumber)
        var albumRating : TextView = itemView.findViewById(R.id.albumRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.list_album,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = albums[position]

        Glide.with(context)
            .load(album.albumThumb)
            .centerCrop()
            .placeholder(R.drawable.ic_signup)
            .into(holder.albumThumb)
        holder.albumTitle.text = album.albumTitle
        holder.albumSongNumber.text = album.albumSongNumber
        holder.albumRating.text = album.albumRating
    }

    override fun getItemCount(): Int {
        return albums.size
    }
}