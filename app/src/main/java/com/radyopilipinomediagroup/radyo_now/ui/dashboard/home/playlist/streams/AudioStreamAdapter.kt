package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.streams

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistContentsResultModel
import com.radyopilipinomediagroup.radyo_now.model.stations.StationContentsResultModel
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.favorites.FavoritesPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.featuredstreaming.FeaturedStreamFragment
import com.radyopilipinomediagroup.radyo_now.utils.BranchObject
import com.radyopilipinomediagroup.radyo_now.utils.Constants
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.guestWarningDialog
import java.lang.reflect.Field
import java.lang.reflect.Method

class AudioStreamAdapter(var context : Context, var programs: List<StationContentsResultModel.Data>, var stationId: Int?) : RecyclerView.Adapter<AudioStreamAdapter.ViewHolder>(){

    var audioStreamFragment = (context as FragmentActivity).supportFragmentManager.findFragmentByTag("AudioStreamFragment") as AudioStreamFragment
    var favoriteListener = audioStreamFragment as FavoritesPresenter.FavoriteCallback

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var programThumb: ImageView = itemView.findViewById(R.id.faveThumb)
        var programTitle: TextView = itemView.findViewById(R.id.faveTitle)
        var programSubtitle: TextView = itemView.findViewById(R.id.faveSubtitle)
        var programDate: TextView = itemView.findViewById(R.id.faveRating)
        var otherOption: ImageView = itemView.findViewById(R.id.otherOption)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_favorites,parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return programs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val program = programs[position]

        try {
            holder.programTitle.text = program.name
            holder.programSubtitle.text = program.description
            holder.programDate.text = Services.convertDate(program.broadcast_date.toString())
            Glide.with(context)
                .load(program.thumbnail)
                .centerCrop()
                .placeholder(R.drawable.ic_no_image)
                .into(holder.programThumb)
        } catch (e : Exception) {
            print(e.message)
        }

        holder.otherOption.setOnClickListener {
            val wrapper = ContextThemeWrapper(
                context,
                R.style.popupMenu
            )
            val popup = PopupMenu(wrapper, it)
            popup.menuInflater?.inflate(R.menu.popup_menu, popup.menu)
            val likeMenu = popup.menu.findItem(R.id.like)
            if(program.isAppFavorite!!) {
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
                        (context as DashboardActivity).passData(programs[position].id!!)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.like -> {
                        if(audioStreamFragment.checkRestriction(Constants.ADD_FAVORITES) == false)  return@setOnMenuItemClickListener false
                        if(program.isAppFavorite!!) favoriteListener.deleteFavorite(program.id.toString())
                        else favoriteListener.addToFavorites(program.id.toString())
                        return@setOnMenuItemClickListener true
                    }
                    R.id.shareContent -> {
                        shareContent(program)
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }
            popup.show()
        }

        holder.itemView.setOnClickListener {
            if(program.featured!!){
                if(audioStreamFragment.checkRestriction(Constants.FEATURED) == false)  return@setOnClickListener
            }
            if (program.format == "youtube"
                || program.format == "video") {
                setFragmentDetails(FeaturedStreamFragment(), stationId.toString(),
                    program.id.toString(), program.name.toString(), program.type.toString(),"FeaturedStreamFragment")
            } else {
                setFragmentDetails(AudioStreamFragment(), stationId.toString(),
                    program.id.toString(), program.name.toString(), program.type.toString(),"AudioStreamFragment")
            }
        }
    }

    private fun shareContent(data: StationContentsResultModel.Data){
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

    private fun setForceShowIcon(popupMenu: PopupMenu) {
        try {
            val fields: Array<Field> = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.getName()) {
                    field.isAccessible = true
                    val menuPopupHelper: Any = field.get(popupMenu)!!
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


    private fun setFragmentDetails(fragment: Fragment, stationId: String, contentId: String,
                                   stationName: String, stationType: String, tag: String) {
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