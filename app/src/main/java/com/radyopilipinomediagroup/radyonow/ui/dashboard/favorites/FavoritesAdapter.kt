package com.radyopilipinomediagroup.radyonow.ui.dashboard.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.radyopilipinomediagroup.radyonow.R
import com.radyopilipinomediagroup.radyonow.model.FavoritesModel

class FavoritesAdapter(var context : Context, var favorites : List<FavoritesModel>) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var faveThumb : ImageView = itemView.findViewById(R.id.faveThumb)
        var faveTitle : TextView = itemView.findViewById(R.id.faveTitle)
        var faveSubtitle : TextView = itemView.findViewById(R.id.faveSubtitle)
        var faveRating : TextView = itemView.findViewById(R.id.faveRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_favorites, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritesAdapter.ViewHolder, position: Int) {
        val fave = favorites[position]

        Glide.with(context)
            .load(fave.faveThumb)
            .centerCrop()
            .placeholder(R.drawable.ic_signup)
            .into(holder.faveThumb)
        holder.faveTitle.text = fave.faveTitle
        holder.faveSubtitle.text = fave.faveSubtitle
        holder.faveRating.text = fave.faveRating


    }

    override fun getItemCount(): Int {
        return favorites.size
    }
 }