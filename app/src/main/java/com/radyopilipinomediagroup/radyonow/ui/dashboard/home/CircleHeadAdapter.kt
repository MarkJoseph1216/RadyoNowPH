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

class CircleHeadAdapter(var context : Context, var images : List<PostModel>) : RecyclerView.Adapter<CircleHeadAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var headImage: ImageView = itemView.findViewById(R.id.circularPic)
        var stationName: TextView = itemView.findViewById(R.id.stationName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.list_circle_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images[position]
            if (!image.postThumb.isNullOrEmpty()) Glide.with(context)
                .load(image.postThumb)
                .centerCrop()
                .into(holder.headImage)
        holder.stationName.text = image.profileName
    }

    override fun getItemCount(): Int {
        return images.size
    }
}