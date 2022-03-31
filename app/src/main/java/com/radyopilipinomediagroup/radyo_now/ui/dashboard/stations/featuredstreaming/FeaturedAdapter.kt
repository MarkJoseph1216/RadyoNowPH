package com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.featuredstreaming

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.programs.FeaturedProgramsResultModel
import com.radyopilipinomediagroup.radyo_now.model.stations.StationContentsResultModel
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.favorites.FavoritesPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.streams.AudioStreamFragment
import com.radyopilipinomediagroup.radyo_now.utils.*
import java.lang.reflect.Field
import java.lang.reflect.Method


class FeaturedAdapter(
    var context: Context, var streams: List<StationContentsResultModel.Data>,
    var stationId: String, var stationName: String
) : RecyclerView.Adapter<FeaturedAdapter.ViewHolder>() {

    var featuredStreamFragment = (context as FragmentActivity).supportFragmentManager.findFragmentByTag("FeaturedStreamFragment") as FeaturedStreamFragment
    var favoriteListener = featuredStreamFragment as FavoritesPresenter.FavoriteCallback

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var streamThumb : ImageView = itemView.findViewById(R.id.faveThumb)
        var streamTitle : TextView = itemView.findViewById(R.id.faveTitle)
        var streamSubtitle : TextView = itemView.findViewById(R.id.faveSubtitle)
        var streamRating : TextView = itemView.findViewById(R.id.faveRating)
        var otherOption: ImageView = itemView.findViewById(R.id.otherOption)
        var layoutLiveHolder : LinearLayout = itemView.findViewById(R.id.layoutLiveHolder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_favorites,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = try {
        val stream = streams[position]
        if (stationName != stream.name) {
            Glide.with(context)
                .load(stream.thumbnail)
                .placeholder(R.drawable.ic_radyo_icon)
                .into(holder.streamThumb)
            holder.streamTitle.text = stream.name
            holder.streamSubtitle.text = stream.description
            holder.streamRating.text = Services.convertDate(stream.broadcast_date.toString())

            holder.itemView.setOnClickListener {
                if(stream.featured!!){
                    if(featuredStreamFragment.checkRestriction(Constants.FEATURED) == false)  return@setOnClickListener
                }
                if (stream.format == "youtube"
                    || stream.format == "video"
                ) {
                    setFragmentDetails(
                        FeaturedStreamFragment(),
                        stationId,
                        stream.id.toString(),
                        stream.name.toString(),
                        stream.type.toString(),
                        "FeaturedStreamFragment"
                    )
                } else {
                    setFragmentDetails(
                        AudioStreamFragment(),
                        stationId,
                        stream.id.toString(),
                        stream.name.toString(),
                        stream.type.toString(),
                        "AudioStreamFragment"
                    )
                }
            }
            holder.otherOption.setOnClickListener {
                showPopUpMenu(
                    holder.otherOption,
                    stream.id.toString(),
                    stream.isAppFavorite!!,
                    stream)
            }

            if (stream.type == "stream")
                holder.layoutLiveHolder.visibility = View.VISIBLE
            else
                holder.layoutLiveHolder.visibility = View.GONE
        } else {
            if (streams.size == 1) featuredStreamFragment.setNoPrograms()
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    } catch (e: Exception) {
        log(e.message.toString())
    }

    private fun showPopUpMenu(
        imgOption: ImageView,
        stationId: String,
        isFavorite: Boolean,
        data: StationContentsResultModel.Data) {

        val wrapper = ContextThemeWrapper(
            context,
            R.style.popupMenu
        )
        val popup = PopupMenu(wrapper, imgOption)
        popup.menuInflater?.inflate(R.menu.popup_menu_stream, popup.menu)
        val likeMenu = popup.menu.findItem(R.id.like)
        if(isFavorite) {
            likeMenu.title = "Unlike"
            likeMenu.icon = ContextCompat.getDrawable(context, R.drawable.ic_favorite_icon)
        }
        else {
            likeMenu.title = "Like"
            likeMenu.icon =
                ContextCompat.getDrawable(context, R.drawable.ic_favorite_no_outline_icon_gray)
        }
        setForceShowIcon(popup)
        popup.setOnMenuItemClickListener{ menu ->
            when (menu.itemId) {
                R.id.addToPlaylist -> {
                    try {
                        (context as DashboardActivity).passData(stationId.toInt())
                    } catch (e: java.lang.Exception) {
                    }
                    return@setOnMenuItemClickListener true
                }
                R.id.shareContent -> {
                    shareContent(data)
                    return@setOnMenuItemClickListener true
                }
                R.id.like -> {
                    if(featuredStreamFragment.checkRestriction(Constants.ADD_FAVORITES) == false)  return@setOnMenuItemClickListener false
                    if(isFavorite) favoriteListener.deleteFavorite(stationId)
                    else favoriteListener.addToFavorites(stationId)
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
        popup.show()
    }

    private fun shareContent(data: StationContentsResultModel.Data){
        val buo = BranchObject.buo
        buo.title = data.name
        buo.setContentDescription(data.description)
        BranchObject.setLinkProperties(data.name!!, data.id.toString(), data.format!!)

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

    private fun setFragmentDetails(
        fragment: Fragment, stationId: String,
        contentId: String, stationName: String, stationType: String, tag: String
    ) {
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

    override fun getItemCount(): Int {
        return streams.size
    }
 }