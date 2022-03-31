package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.notification

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.streams.AudioStreamFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.featuredstreaming.FeaturedStreamFragment
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.model.realm.NotificationLocal
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(var context: Context, var notifications: List<NotificationLocal>): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    var interfaces = (context as FragmentActivity).supportFragmentManager.findFragmentByTag("NotificationFragment") as NotificationFragment

    class NotificationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var contentTitle: TextView = itemView.findViewById(R.id.contentTitle)
        var contentDesc: TextView = itemView.findViewById(R.id.contentDesc)
        var contentDate: TextView = itemView.findViewById(R.id.contentDate)
        var mainContainer: ViewGroup = itemView.findViewById(R.id.mainContainer)
        var progressLoading: ProgressBar = itemView.findViewById(R.id.progressLoading)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_notification, parent, false)
        return NotificationViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]

        /** Check first if the notification is read or unread
         * if unread the background color is notifRed(#80B9282F)
         * else the background is white */
//        if(notification.title.isNullOrEmpty()) holder.mainContainer.visibility = View.GONE
//        else holder.mainContainer.visibility = View.VISIBLE
        println("onBindViewHolder: $position - ${notification.isRead!!}")
        if(!notification.isRead!!) holder.mainContainer.setBackgroundColor(ContextCompat.getColor(context,R.color.notifRed))
        else holder.mainContainer.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
        holder.contentTitle.text = notification.title
        holder.contentDesc.text = notification.body
        holder.contentDate.text = Services.convertDate(notification.date!!,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","MMMM dd, yyyy")

        println("itemView: ${notification.format} - ${notification.contentId}")
        holder.itemView.setOnClickListener {
            interfaces.updateNotificationRead(notification)
            when (notification.format) {
                "youtube",
                "video" -> {
                    setFragmentDetails(
                        FeaturedStreamFragment(), notification.contentId?.toInt()!!, notification.contentId?.toInt()!!,
                        "", notification.format!!,"FeaturedStreamFragment")
                }
                "audio" -> {
                    setFragmentDetails(
                        AudioStreamFragment(), notification.contentId?.toInt()!!, notification.contentId?.toInt()!!,
                        "", notification.format!!, "AudioStreamFragment")
                }
            }
        }

        holder.progressLoading.visibility = if(position == notifications.lastIndex
            && interfaces.getCurrentPage()!! < interfaces.getPageCount()!!) {
            interfaces.getNextPage()
            View.VISIBLE
        } else View.GONE
    }

    private fun convertLongToDate(millis: Long): String{
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis

        val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return outputFormat.format(cal.time)
    }
    override fun getItemCount(): Int = notifications.size

    fun setFragmentDetails(fragment: Fragment, stationId: Int, contentId: Int,
                           stationName: String, stationType: String, tag: String) {
        Services.changeFragment(
            (context as FragmentActivity).supportFragmentManager,
            fragment,
            tag,
            stationId.toString(),
            contentId.toString(),
            stationName,
            stationType
        )
    }
}