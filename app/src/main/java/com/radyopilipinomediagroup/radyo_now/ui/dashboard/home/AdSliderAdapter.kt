package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.utils.Services

class AdSliderAdapter(var context: Context, var pics : List<AdsModel.Data.Assets>):
    RecyclerView.Adapter<AdSliderAdapter.ViewHolder> (){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var adThumb : ImageView = itemView.findViewById(R.id.adThumb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_ad_slider, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pic = pics[position]
        Glide.with(context)
            .load(pic.imageUrl)
            .into(holder.adThumb)

        holder.itemView.setOnClickListener {
            try {
                Services.openBrowser(context, pic.link!!)
            } catch (e: Exception) {}
        }
    }

    override fun getItemCount(): Int {
        return pics.size
    }
}