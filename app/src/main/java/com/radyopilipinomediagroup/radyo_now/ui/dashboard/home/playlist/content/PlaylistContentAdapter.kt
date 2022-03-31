package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.content

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
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistContentsResultModel
import com.radyopilipinomediagroup.radyo_now.model.stations.StationContentsResultModel
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.streams.AudioStreamFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.featuredstreaming.FeaturedStreamFragment
import com.radyopilipinomediagroup.radyo_now.utils.BranchObject
import com.radyopilipinomediagroup.radyo_now.utils.Constants
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.guestWarningDialog
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Method

class PlaylistContentAdapter(var context : Context, var playlists : List<PlaylistContentsResultModel.Data>) : RecyclerView.Adapter<PlaylistContentAdapter.ViewHolder>() {

    val playlistContentFragment = (context as FragmentActivity).supportFragmentManager.findFragmentByTag("PlaylistContentFragment") as PlaylistContentFragment
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var faveThumb : ImageView = itemView.findViewById(R.id.faveThumb)
        var faveTitle : TextView = itemView.findViewById(R.id.faveTitle)
        var faveSubtitle : TextView = itemView.findViewById(R.id.faveSubtitle)
        var faveRating : TextView = itemView.findViewById(R.id.faveRating)
        var otherOption : ImageView = itemView.findViewById(R.id.otherOption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_playlist_3, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        Glide.with(context)
            .load(playlist.thumbnail)
            .placeholder(R.drawable.ic_no_image)
            .into(holder.faveThumb)
        holder.faveTitle.text = playlist.name
        holder.faveSubtitle.text = playlist.description
        holder.faveRating.text = Services.convertDate(playlist.broadcastDate.toString())

        holder.otherOption.setOnClickListener {
            val wrapper = ContextThemeWrapper(
                context,
                R.style.popupMenu
            )
            val popup = PopupMenu(wrapper, it)
            popup.menuInflater?.inflate(R.menu.popup_menu_playlist_content, popup.menu)
            setForceShowIcon(popup)
            popup.setOnMenuItemClickListener{ menu ->
                when (menu.itemId) {
                    R.id.removeContent -> {
                        try {
                            val playlistContentFragment = (context as FragmentActivity).supportFragmentManager.findFragmentByTag("PlaylistContentFragment")
                            (playlistContentFragment as PlaylistContentFragment).onRemoveContent(playlists[position].id!!)
                        }catch (e:Exception){}
                        return@setOnMenuItemClickListener true
                    }
                    R.id.shareContent -> {
                        shareContent(playlist)
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }
            popup.show()
        }

        holder.itemView.setOnClickListener {
            if(playlist.featured!!){
                if(playlistContentFragment.checkRestriction(Constants.FEATURED) == false)  return@setOnClickListener
            }
            if (playlist.format == "youtube"
                || playlist.format == "video") {
                setFragmentDetails(FeaturedStreamFragment(), playlist.program.id.toString(),
                    playlist.id.toString(), playlist.name.toString(), playlist.type.toString(),"FeaturedStreamFragment")
            } else {
                setFragmentDetails(AudioStreamFragment(), playlist.program.id.toString(),
                    playlist.id.toString(), playlist.name.toString(), playlist.type.toString(),"AudioStreamFragment")
            }
        }
    }

    private fun shareContent(data: PlaylistContentsResultModel.Data){
        val buo = BranchObject.buo
        buo.title = data.name
        buo.setContentDescription(data.description)
        BranchObject.setLinkProperties(data.name!!,data.id.toString(),data.format!!)

        buo.generateShortUrl(context, BranchObject.linkProperties!!
        ) { url, error ->
            println("shareContent: ${error == null}")
            val newUrl = if(error == null) url
            else "https://radyopilipino.app.link/${data.id}"
            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.putExtra(Intent.EXTRA_TEXT, newUrl)
            share.type = "text/plain"
            (context as Activity).startActivity(Intent.createChooser(share, "Share via..."))
        }
    }

    private fun setFragmentDetails(fragment: Fragment, stationId: String,
                                   contentId: String, stationName: String, stationType: String, tag:String) {
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
        return playlists.size
    }
 }