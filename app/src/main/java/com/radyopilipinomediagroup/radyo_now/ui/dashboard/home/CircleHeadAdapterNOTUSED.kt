package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.PostModel
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.featured.FeaturedFragment

class CircleHeadAdapterNOTUSED(var context : Context, var images : List<PostModel>) : RecyclerView.Adapter<CircleHeadAdapterNOTUSED.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var headImage: ImageView = itemView.findViewById(R.id.circularPic)
        var stationButton: ViewGroup = itemView.findViewById(R.id.stationButton)
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
        holder.stationButton.setOnClickListener {
            val fragmentManager = (context as FragmentActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.dashboardFrame, FeaturedFragment())
                .addToBackStack("StationsFragment")
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }
}