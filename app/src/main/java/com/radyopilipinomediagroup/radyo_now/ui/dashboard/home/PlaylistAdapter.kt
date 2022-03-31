package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistListResultModel
import com.radyopilipinomediagroup.radyo_now.ui.AbstractInterface
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.PlaylistThumbAdapter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.content.PlaylistContentFragment
import com.radyopilipinomediagroup.radyo_now.utils.Constants
import com.radyopilipinomediagroup.radyo_now.utils.Services

class PlaylistAdapter(var context: Context, var playlists: List<PlaylistListResultModel.Data>):
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    val homeFragment = (context as FragmentActivity).supportFragmentManager.findFragmentByTag("HomeFragment") as HomeFragment
    val listener = homeFragment as HomePresenter.CreatePlaylist
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var playlistThumb : ShapeableImageView = itemView.findViewById(R.id.playlistThumb)
        var playlistItems : TextView = itemView.findViewById(R.id.playlistItems)
        var playlistName : TextView = itemView.findViewById(R.id.playlistName)
        var recycler : RecyclerView = itemView.findViewById(R.id.thumbContainer)
        var createPlaylistBtn: ImageView = itemView.findViewById(R.id.createPlaylistBtn)
        var grid = StaggeredGridLayoutManager(2,
            StaggeredGridLayoutManager.VERTICAL)
//        GridLayoutManager(itemView.context, 2, GridLayoutManager.VERTICAL,false)
        var llm = LinearLayoutManager(itemView.context)
        var adapter : PlaylistThumbAdapter? = null
        private val radius = itemView.context.resources.getDimension(R.dimen.corner_radius)
//        val imageShapeOdd = playlistThumb.shapeAppearanceModel
//            .toBuilder()
//            .setTopLeftCorner(CornerFamily.ROUNDED,radius)
//            .setBottomRightCorner(CornerFamily.ROUNDED,radius)
//            .build()
//        val imageShapeEven = playlistThumb.shapeAppearanceModel
//            .toBuilder()
//            .setBottomLeftCorner(CornerFamily.ROUNDED,radius)
//            .setTopRightCorner(CornerFamily.ROUNDED,radius)
//            .build()
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_playlist, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position == playlists.size){
            holder.createPlaylistBtn.visibility = View.VISIBLE
            holder.playlistName.visibility = View.INVISIBLE
            holder.recycler.visibility = View.INVISIBLE
            holder.itemView.setOnClickListener {
                listener.onAdapterCreatePlaylistClick()
            }
            return
        }

        holder.createPlaylistBtn.visibility = View.GONE
        holder.playlistName.visibility = View.VISIBLE
        holder.recycler.visibility = View.VISIBLE

        val playlist = playlists[position]
        println("THUMBNAILS: ${playlist.thumbnail!!}")
        holder.adapter = PlaylistThumbAdapter(context, playlists[position])
        holder.recycler.adapter = holder.adapter
        holder.recycler.layoutManager = if(playlist.thumbnail?.size!! > 1) holder.grid else holder.llm
        holder.adapter?.notifyDataSetChanged()

        //Displaying latest top 3 only and hiding the other playlists data.
        if (playlists.size > 3) if (position > 2) {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }

//        Log.d("POSITION", "${position % 2 == 0}")
//        if(position % 2 == 0) holder.playlistThumb.shapeAppearanceModel = holder.imageShapeEven
//        else holder.playlistThumb.shapeAppearanceModel = holder.imageShapeOdd

//        if (position == 0) {
//            holder.params.setMargins(55, 28, 15, 28)
//            holder.itemView.layoutParams = holder.params
//        }
//        else if (position == playlists.size -1){
//            holder.params.setMargins(15, 28, 55, 28)
//            holder.itemView.layoutParams = holder.params
//        }

        println(playlist.name)
        holder.playlistName.text = playlist.name
        holder.playlistItems.text = if(playlist.contentsCount!! > 1) "${playlist.contentsCount} items" else "${playlist.contentsCount} item"
//        when(playlist.thumbnail?.size){
//
//        }
//        Glide.with(context).load().placeholder(R.drawable.ic_no_image).into(holder.playlistThumb)

        holder.itemView.setOnClickListener{
            val fragmentManager = (context as FragmentActivity).supportFragmentManager
            val playlistContentFragment = PlaylistContentFragment()
            Services.changeFragment(fragmentManager,playlistContentFragment,"PlaylistContentFragment")
            (playlistContentFragment as AbstractInterface.DataHandler<PlaylistListResultModel.Data>).passData(playlists[position])
        }
    }

    override fun getItemCount(): Int {
//        return if(playlists.size == 3) 4
//        else
            return playlists.size + 1
    }
}