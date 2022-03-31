package com.radyopilipinomediagroup.radyo_now.ui.dashboard.comments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.comments.CommentsResponse
import com.radyopilipinomediagroup.radyo_now.utils.Services

class CommentAdapter(var context : Context, var comments : List<CommentsResponse.Data>)
    : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileThumb : CircularImageView = itemView.findViewById(R.id.profileThumb)
        var txtUserName : TextView = itemView.findViewById(R.id.txtUserName)
        var txtComment : TextView = itemView.findViewById(R.id.txtComment)
        var txtDate : TextView = itemView.findViewById(R.id.txtDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_comments, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val data = comments[position]
            Glide.with(context)
                .load(data.appUser?.avatar)
                .centerCrop()
                .placeholder(R.drawable.ic_radyo_icon)
                .into(holder.profileThumb)

            holder.txtUserName.text = data.appUser?.name
            holder.txtComment.text = data.comment
            holder.txtDate.text = Services.convertTimeZoneDate(data.createdAt.toString())
        } catch (e: Exception) {}
    }

    override fun getItemCount(): Int {
        return comments.size
    }
 }