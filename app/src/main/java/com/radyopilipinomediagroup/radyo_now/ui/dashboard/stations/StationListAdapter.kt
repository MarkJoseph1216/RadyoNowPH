package com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.stations.StationListResultModel
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.channel.ChannelFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.channel.StationDetailsHandler
import com.radyopilipinomediagroup.radyo_now.utils.Services

class StationListAdapter(var context: Context, var stations: List<StationListResultModel.Station>):
    RecyclerView.Adapter<StationListAdapter.ViewHolder>() {

    private var firebaseAnalytics: FirebaseAnalytics? = Firebase.analytics

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var stationName: TextView = itemView.findViewById(R.id.stationName)
        var stationLogo: ShapeableImageView = itemView.findViewById(R.id.stationLogo)
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_stations, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val station = stations[position]

//        if(position == 1) {
//            holder.params.setMargins(0, 150, 0, 0)
//            holder.itemView.layoutParams = holder.params
//        }
        holder.stationName.text = station.name
        Glide.with(context)
            .load(station.logo)
            .placeholder(R.drawable.ic_no_image)
            .into(holder.stationLogo)

        holder.itemView.setOnClickListener{
            Services.setDataAnalytics(firebaseAnalytics, "Channels", "app_screen_view")
            val channelFragment = ChannelFragment()
            Services.changeFragment((context as FragmentActivity).supportFragmentManager,channelFragment, "ChannelFragment")
            (channelFragment as StationDetailsHandler).stationDetails(stations[position].id!!.toInt())
        }
    }

    override fun getItemCount(): Int {
        return stations.size
    }
}