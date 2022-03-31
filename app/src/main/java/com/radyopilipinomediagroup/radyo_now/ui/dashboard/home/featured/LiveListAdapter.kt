package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.featured

import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.stations.StationContentsResultModel
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.streams.AudioStreamFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.featuredstreaming.FeaturedStreamFragment
import com.radyopilipinomediagroup.radyo_now.utils.Services

class LiveListAdapter(var context : Context, var lives: List<StationContentsResultModel.Data>?, var stationID: Int) : RecyclerView.Adapter<LiveListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var liveThumb: ImageView = itemView.findViewById(R.id.playlistThumb)
        var liveRadioStation: TextView = itemView.findViewById(R.id.playlistName)
        var liveRadioStationSubtitle: TextView = itemView.findViewById(R.id.liveRadioStationSubtitle)
        var liveRating: TextView = itemView.findViewById(R.id.playlistItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_live_big, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lives!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val live = lives?.get(position)
//        val params: RelativeLayout.LayoutParams =
//            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
//        if(position == 0){
//            params.setMargins(55,28,15,28)
//            holder.itemView.layoutParams = params
//        } else if (position == lives!!.size-1){
//            params.setMargins(15,28,55,28)
//            holder.itemView.layoutParams = params
//        }
        holder.liveRadioStation.text = live!!.name
        holder.liveRadioStationSubtitle.text = live.description
        Glide.with(context)
            .load(live.thumbnail)
            .placeholder(R.drawable.ic_no_image)
            .into(holder.liveThumb)

        holder.itemView.setOnClickListener {
            if (live.format == "youtube"
                || live.format == "video") {
                setFragmentDetails(FeaturedStreamFragment(), stationID.toString(),
                    live.id.toString(), live.name.toString(), live.type.toString(),"FeaturedStreamFragment")
            } else {
                setFragmentDetails(AudioStreamFragment(), stationID.toString(),
                    live.id.toString(), live.name.toString(), live.type.toString(),"AudioStreamFragment")
            }
        }
    }

    private fun setFragmentDetails(fragment: Fragment, stationId: String, contentId: String,
                                   stationName: String, stationType: String, tag : String) {
        Services.changeFragment(
            (context as FragmentActivity).supportFragmentManager,
            fragment,
            tag,
            stationId,
            contentId,
            stationName,
            stationType
        )
    }
}