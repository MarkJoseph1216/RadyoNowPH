package com.radyopilipinomediagroup.radyonow.ui.dashboard.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.radyopilipinomediagroup.radyonow.R
import com.radyopilipinomediagroup.radyonow.model.PostModel
import com.radyopilipinomediagroup.radyonow.utils.Services

class TrendingAdapter(var context : Context, var posts : List<PostModel>) : RecyclerView.Adapter<TrendingAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profilePic: ImageView = itemView.findViewById(R.id.profilePic)
        var share: ImageView = itemView.findViewById(R.id.share)
        var download: ImageView = itemView.findViewById(R.id.download)
        var postThumb: ImageView = itemView.findViewById(R.id.postThumb)
        var profileName: TextView = itemView.findViewById(R.id.profileName)
        var postTime: TextView = itemView.findViewById(R.id.postTime)
        var postTitle: TextView = itemView.findViewById(R.id.postTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_trending, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val post = posts[position]

        holder.profileName.text = post.profileName
        holder.postTime.text = post.postTime
        holder.postTitle.text = post.postTitle


        Glide.with(context).load(post.profilePicture).placeholder(R.drawable.ic_signup).into(holder.profilePic)
        Glide.with(context).load(post.postThumb).placeholder(R.drawable.ic_signup).into(holder.postThumb)

        holder.share.setOnClickListener{ Services.notAvailable(context) }
        holder.download.setOnClickListener{ Services.notAvailable(context) }

    }

    override fun getItemCount(): Int {
       return posts.size
    }

}