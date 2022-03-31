package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.programs.FeaturedProgramsResultModel
import com.radyopilipinomediagroup.radyo_now.model.stations.FeaturedStationsResultModel
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.streams.AudioStreamFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.channel.ChannelFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.channel.StationDetailsHandler
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.featuredstreaming.FeaturedStreamFragment
import com.radyopilipinomediagroup.radyo_now.utils.BranchObject
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.guestWarningDialog
import java.lang.reflect.Field
import java.lang.reflect.Method


class TrendingAdapter(var context: Context, var posts: List<FeaturedStationsResultModel.Data>) : RecyclerView.Adapter<TrendingAdapter.ViewHolder>() {

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
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_trending,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]

        try {
            holder.profileName.text = post.name
            holder.postTime.text = Services.convertDate(post.featured!!.broadcast_date.toString())
            holder.postTitle.text = post.featured?.name

            Glide.with(context).load(post.logo).placeholder(R.drawable.ic_no_image_2)
                .into(holder.profilePic)
            Glide.with(context).load(post.featured?.thumbnail).placeholder(R.drawable.ic_no_image)
                .into(holder.postThumb)
        } catch (e : Exception){}

        holder.profileName.setOnClickListener { openChannelFragment(post.id!!) }
        holder.profilePic.setOnClickListener { openChannelFragment(post.id!!) }
        holder.share.setOnClickListener{
            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.putExtra(Intent.EXTRA_TEXT, post.featured?.contentUrl)
            share.type = "text/plain"
            (context as Activity).startActivity(Intent.createChooser(share, "Share via..."))
        }

        holder.download.setOnClickListener{ val wrapper = ContextThemeWrapper(
            context,
            R.style.popupMenu
        )
            val popup = PopupMenu(wrapper, it)
            popup.menuInflater?.inflate(R.menu.popup_menu, popup.menu)
            setForceShowIcon(popup)
            popup.setOnMenuItemClickListener{ menu ->
                when (menu.itemId) {
                    R.id.addToPlaylist -> {
                        (context as DashboardActivity).passData(posts[position].id!!)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.like -> {
                        Services.notAvailable(context)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.shareContent -> {
                        shareContent(post)
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }
            popup.show()
        }

        holder.itemView.setOnClickListener {
            if (post.featured!!.format == "youtube"
                || post.featured!!.format == "video") {
                setFragmentDetails(FeaturedStreamFragment(), post.id!!, post.featured!!.id!!,
                    post.name!!, post.type!!,"FeaturedStreamFragment")
            } else {
                setFragmentDetails(AudioStreamFragment(), post.id!!, post.featured!!.id!!,
                    post.name!!, post.type!!, "AudioStreamFragment")
            }
        }
    }

    private fun shareContent(data: FeaturedStationsResultModel.Data){
        val buo = BranchObject.buo
        buo.title = data.featured?.name
        BranchObject.setLinkProperties(data.featured?.name!!, data.featured?.id.toString(), data.featured?.format!!)

        buo.generateShortUrl(context, BranchObject.linkProperties!!
        ) { url, error ->
            println("shareContent: ${error == null}")
            val newUrl = if(error == null) url
            else "https://radyopilipino.app.link/${data.featured?.id}"
            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.putExtra(Intent.EXTRA_TEXT, newUrl)
            share.type = "text/plain"
            (context as Activity).startActivity(Intent.createChooser(share, "Share via..."))

        }
    }

    private fun openChannelFragment(stationId: Int){
        val channelFragment = ChannelFragment()
        Services.changeFragment((context as FragmentActivity).supportFragmentManager,channelFragment, "ChannelFragment")
        (channelFragment as StationDetailsHandler).stationDetails(stationId)
    }

    private fun setFragmentDetails(fragment: Fragment, stationId: Int, contentId: Int,
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

    private fun setForceShowIcon(popupMenu: PopupMenu) {
        try {
            val fields: Array<Field> = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.getName()) {
                    field.setAccessible(true)
                    val menuPopupHelper: Any = field.get(popupMenu)
                    val classPopupHelper = Class.forName(
                        menuPopupHelper
                            .javaClass.name
                    )
                    val setForceIcons: Method = classPopupHelper.getMethod(
                        "setForceShowIcon", Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
       return posts.size
    }
}