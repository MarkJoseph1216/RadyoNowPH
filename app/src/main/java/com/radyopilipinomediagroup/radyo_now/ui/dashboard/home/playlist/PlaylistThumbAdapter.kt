package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistListResultModel
import com.radyopilipinomediagroup.radyo_now.ui.AbstractInterface
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.content.PlaylistContentFragment
import com.radyopilipinomediagroup.radyo_now.utils.Services

class PlaylistThumbAdapter(var context: Context, var playlists: PlaylistListResultModel.Data) : RecyclerView.Adapter<PlaylistThumbAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var playlistThumb : ImageView = itemView.findViewById(R.id.playlistThumb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_playlist_thumb,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val thumb = playlists.thumbnail!![position]
        if(playlists.thumbnail?.size!! >= 3){
            holder.playlistThumb.layoutParams.height = pxToDp(40F).toInt()
        }else holder.playlistThumb.layoutParams.height = pxToDp(80F).toInt()
        holder.playlistThumb.requestLayout()
        initComponents(holder, thumb)
        initListener(holder)
    }

    private fun initListener(holder: ViewHolder) {
        holder.itemView.setOnClickListener {
            val fragmentManager = (context as FragmentActivity).supportFragmentManager
            val playlistContentFragment = PlaylistContentFragment()
            Services.changeFragment(fragmentManager,playlistContentFragment,"PlaylistContentFragment")
            (playlistContentFragment as AbstractInterface.DataHandler<PlaylistListResultModel.Data>).passData(playlists)
        }
    }

    private fun initComponents(holder: ViewHolder, thumb: String){
        Glide.with(context)
            .load(thumb)
            .placeholder(R.drawable.ic_no_image)
            .into(holder.playlistThumb)
    }

    fun  pxToDp(size: Float) : Float{
        val r: Resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            size,
            r.displayMetrics
        )
    }

    override fun getItemCount(): Int {
        return playlists.thumbnail?.size!!
    }
}