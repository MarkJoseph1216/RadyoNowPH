package com.radyopilipinomediagroup.radyo_now.ui.dashboard.favorites

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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.stations.FeaturedStationsResultModel
import com.radyopilipinomediagroup.radyo_now.model.stations.StationContentsResultModel
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.streams.AudioStreamFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.featuredstreaming.FeaturedStreamFragment
import com.radyopilipinomediagroup.radyo_now.utils.BranchObject
import com.radyopilipinomediagroup.radyo_now.utils.Constants
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.guestWarningDialog
import java.lang.reflect.Field
import java.lang.reflect.Method

class FavoritesListAdapter(var context : Context, var favorites : List<StationContentsResultModel.Data>) : RecyclerView.Adapter<FavoritesListAdapter.ViewHolder>() {

    private var firebaseAnalytics: FirebaseAnalytics? = Firebase.analytics
    var favoritesFragment = (context as FragmentActivity).supportFragmentManager.findFragmentByTag("FavoritesFragment") as FavoritesFragment
    var favoriteListener = favoritesFragment as FavoritesPresenter.FavoriteCallback

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var faveThumb : ImageView = itemView.findViewById(R.id.faveThumb)
        var faveTitle : TextView = itemView.findViewById(R.id.faveTitle)
        var faveSubtitle : TextView = itemView.findViewById(R.id.faveSubtitle)
        var faveRating : TextView = itemView.findViewById(R.id.faveRating)
        var otherOption: ImageView = itemView.findViewById(R.id.otherOption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_favorites, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fave = favorites[position]
        Glide.with(context)
            .load(fave.thumbnail)
            .centerCrop()
            .placeholder(R.drawable.ic_no_image)
            .into(holder.faveThumb)
        holder.faveTitle.text = fave.name
        holder.faveSubtitle.text = fave.description
        holder.faveRating.text = Services.convertDate(fave.broadcast_date.toString())

        holder.itemView.setOnClickListener {
            Services.setDataAnalytics(firebaseAnalytics, "Favorites", "app_screen_view")
            if(fave.featured!!){
                if(favoritesFragment.checkRestriction(Constants.FEATURED) == false)  return@setOnClickListener
            }
            if (fave.format == "youtube"
                || fave.format == "video") {
                setFragmentDetails(
                    FeaturedStreamFragment(), fave.id!!, fave.id!!,
                    fave.name!!, fave.type!!,"FeaturedStreamFragment")
            } else {
                setFragmentDetails(
                    AudioStreamFragment(), fave.id!!, fave.id!!,
                    fave.name!!, fave.type!!, "AudioStreamFragment")
            }
        }

        holder.otherOption.setOnClickListener {
            val wrapper = ContextThemeWrapper(
                context,
                R.style.popupMenu
            )
            val popup = PopupMenu(wrapper, it)
            popup.menuInflater?.inflate(R.menu.popup_menu_favorites, popup.menu)
            setForceShowIcon(popup)
            popup.setOnMenuItemClickListener{ menu ->
                when (menu.itemId) {
                    R.id.addToPlaylist -> {
                        (context as DashboardActivity).passData(fave.id!!)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.shareContent -> {
                        shareContent(fave)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.delete -> {
                        favoriteListener.deleteFavorite(fave.id.toString())
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }
            popup.show()
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
                if ("mPopup" == field.name) {
                    field.isAccessible = true
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

    override fun getItemCount(): Int {
        return favorites.size
    }
 }