package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.programs.FeaturedProgramsResultModel
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.favorites.FavoritesPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.streams.AudioStreamFragment
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.stations.featuredstreaming.FeaturedStreamFragment
import com.radyopilipinomediagroup.radyo_now.utils.BranchObject
import com.radyopilipinomediagroup.radyo_now.utils.Constants
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.guestWarningDialog
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.util.LinkProperties
import java.lang.reflect.Field
import java.lang.reflect.Method

class HomeContentAdapter(var context: Context, var posts: List<FeaturedProgramsResultModel.Data>): RecyclerView.Adapter<HomeContentAdapter.ContentViewHolder>() {


    var homeFragment = (context as FragmentActivity).supportFragmentManager.findFragmentByTag("HomeFragment") as HomeFragment
    var favoriteListener = homeFragment as FavoritesPresenter.FavoriteCallback

    class ContentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var contentThumb: ImageView = itemView.findViewById(R.id.contentThumb)
        var contentTitle: TextView = itemView.findViewById(R.id.contentTitle)
        var contentDesc: TextView = itemView.findViewById(R.id.contentDesc)
        var contentProgramName: TextView = itemView.findViewById(R.id.contentProgramName)
        var contentShare: ImageView = itemView.findViewById(R.id.contentShare)
        var contentMoreOptions: ImageView = itemView.findViewById(R.id.contentMoreOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_home_content,
            parent,
            false
        )
        return ContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        val post = posts[position]
        Log.d("GetTrendingData: ", "${post.id}")
        Glide.with(context)
            .load(post.thumbnail)
            .into(holder.contentThumb)
        holder.contentTitle.text = post.name
        holder.contentDesc.text = post.description
        holder.contentProgramName.text = post.program.name

        holder.contentShare.setOnClickListener {
            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.putExtra(Intent.EXTRA_TEXT, post.contentUrl)
            share.type = "text/plain"
            (context as Activity).startActivity(Intent.createChooser(share, "Share via..."))
        }
        holder.contentMoreOptions.setOnClickListener {
            val wrapper = ContextThemeWrapper(
                context,
                R.style.popupMenu
            )
            val popup = PopupMenu(wrapper, it)
            popup.menuInflater?.inflate(R.menu.popup_menu, popup.menu)
            val likeMenu = popup.menu.findItem(R.id.like)
            if(posts[position].isFavorite!!) {
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
                        (context as DashboardActivity).passData(posts[position].id!!)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.like -> {
                        if(homeFragment.checkRestriction(Constants.ADD_FAVORITES) == false)  return@setOnMenuItemClickListener false
                        if(post.isFavorite!!) favoriteListener.deleteFavorite(post.id.toString())
                        else favoriteListener.addToFavorites(post.id.toString())
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
            if(post.featured!!){
                if(homeFragment.checkRestriction(Constants.FEATURED) == false)  return@setOnClickListener
            }
            if (post.format == "youtube"
                || post.format == "video") {
                setFragmentDetails(
                    FeaturedStreamFragment(), post.id!!, post.id!!,
                    post.name!!, post.type!!,"FeaturedStreamFragment")
            } else {
                setFragmentDetails(
                    AudioStreamFragment(), post.id!!, post.id!!,
                    post.name!!, post.type!!, "AudioStreamFragment")
            }
        }
    }

    private fun shareContent(data: FeaturedProgramsResultModel.Data){
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

    override fun getItemCount(): Int = posts.size
}