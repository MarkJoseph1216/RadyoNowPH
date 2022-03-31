package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistListResultModel
import com.radyopilipinomediagroup.radyo_now.ui.AbstractInterface
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.content.PlaylistContentFragment
import com.radyopilipinomediagroup.radyo_now.utils.Services
import java.lang.reflect.Field
import java.lang.reflect.Method

class PlaylistProgramAdapter(
    var context: Context,
    var playlists: List<PlaylistListResultModel.Data>,
    var layoutPosition: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    class ViewHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var thumbRecycler : RecyclerView = itemView.findViewById(R.id.thumbRecycler)
//        var txtStationName : TextView = itemView.findViewById(R.id.txtStationName)
//        var txtStationItems : TextView = itemView.findViewById(R.id.txtStationItems)
//        var grid = StaggeredGridLayoutManager(2,
//            StaggeredGridLayoutManager.VERTICAL)
//        var llm = LinearLayoutManager(itemView.context)
//        var adapter : PlaylistHeadThumbAdapter? = null
//    }

    class ViewItemsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recycler : RecyclerView = itemView.findViewById(R.id.faveThumb)
        var faveTitle : TextView = itemView.findViewById(R.id.faveTitle)
        var faveSubtitle : TextView = itemView.findViewById(R.id.faveSubtitle)
        var faveRating : TextView = itemView.findViewById(R.id.faveRating)
        var otherOption : ImageView = itemView.findViewById(R.id.otherOption)
        var grid = StaggeredGridLayoutManager(2,
            StaggeredGridLayoutManager.VERTICAL)
        var llm = LinearLayoutManager(itemView.context)
        var adapter : PlaylistThumbAdapter2? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return if (layoutPosition == 0) {
//            val view = LayoutInflater.from(parent.context).inflate(
//                R.layout.list_header_playlist,
//                parent,
//                false
//            )
//            ViewHeaderHolder(view)
//        } else {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.list_playlist_2,
                parent,
                false
            )
            return ViewItemsHolder(view)
//        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val playlist = playlists[position]

//        if (layoutPosition == 0) {
//            val viewItemsHolder: ViewHeaderHolder = holder as ViewHeaderHolder
//            if (playlist.name.isNullOrEmpty()) viewItemsHolder.itemView.visibility = View.INVISIBLE
//
//            holder.adapter = PlaylistHeadThumbAdapter(context, playlists[position])
//            holder.thumbRecycler.adapter = holder.adapter
//            holder.thumbRecycler.layoutManager = if(playlist.thumbnail?.size!! > 1) holder.grid else holder.llm
//            holder.adapter?.notifyDataSetChanged()
//
//            viewItemsHolder.txtStationName.text = playlist.name
//            viewItemsHolder.txtStationItems.text = if(playlist.contentsCount!! > 1) "${playlist.contentsCount} items" else "${playlist.contentsCount} item"
//
//            viewItemsHolder.itemView.setOnClickListener {
//                val fragmentManager = (context as FragmentActivity).supportFragmentManager
//                val playlistContentFragment = PlaylistContentFragment()
//                Services.changeFragment(
//                    fragmentManager,
//                    playlistContentFragment,
//                    "PlaylistContentFragment"
//                )
//                (playlistContentFragment as AbstractInterface.DataHandler<PlaylistListResultModel.Data>).passData(
//                    playlists[position]
//                )
//            }
//        } else {
            val viewHolder: ViewItemsHolder = holder as ViewItemsHolder

            holder.adapter = PlaylistThumbAdapter2(context, playlists[position])
            holder.recycler.adapter = holder.adapter
            holder.recycler.layoutManager = if(playlist.thumbnail?.size!! > 1) holder.grid else holder.llm
            holder.adapter?.notifyDataSetChanged()

            viewHolder.faveTitle.text = playlist.name
            viewHolder.faveSubtitle.text =
                if (playlist.contentsCount!! > 1) "${playlist.contentsCount} Episodes" else "${playlist.contentsCount} Episode"
            viewHolder.faveRating.visibility = View.GONE

            viewHolder.itemView.setOnClickListener {
                val fragmentManager = (context as FragmentActivity).supportFragmentManager
                val playlistContentFragment = PlaylistContentFragment()
                Services.changeFragment(
                    fragmentManager,
                    playlistContentFragment,
                    "PlaylistContentFragment"
                )
                (playlistContentFragment as AbstractInterface.DataHandler<PlaylistListResultModel.Data>).passData(
                    playlists[position]
                )
            }
            viewHolder.otherOption.setOnClickListener {
                var playlistFragment: Fragment? = null
                try {
                    playlistFragment =
                        (context as FragmentActivity).supportFragmentManager.findFragmentByTag("PlaylistFragment")
                } catch (e: Exception) {
                }

                val wrapper = ContextThemeWrapper(
                    context,
                    R.style.popupMenu
                )
                val popup = PopupMenu(wrapper, it)
                popup.menuInflater?.inflate(R.menu.popup_menu_playlist, popup.menu)
                setForceShowIcon(popup)
                popup.setOnMenuItemClickListener { menu ->
                    when (menu.itemId) {
                        R.id.renamePlaylist -> {
                            (playlistFragment as PlaylistFragment).onRenamePlaylist(playlists[position].id!!, playlists[position].name!!)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.sharePlaylist -> {
                            Services.notAvailable(context)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.deletePlaylist -> {
                            (playlistFragment as PlaylistFragment).onDeletePlaylist(playlists[position].id!!)
                            return@setOnMenuItemClickListener true
                        }
                        else -> return@setOnMenuItemClickListener false
                    }
                }
                popup.show()
            }
//        }
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

//        if (layoutPosition == 0) {
//            /** Checking if the list isNotEmpty to not display two empty layout **/
//            when(playlists.size) {
//                1 -> 1
//                else -> {
//                    if (playlists.isNotEmpty()) { 2 } else { 0 }
//                }
//            }
//            /** **/
//        } else {
        return playlists.size
//        }
    }
 }