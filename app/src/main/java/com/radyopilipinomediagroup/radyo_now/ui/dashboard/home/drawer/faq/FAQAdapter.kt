package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.faq

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.faq.FAQModel

class FAQAdapter(var context : Context, var faqList : List<FAQModel>?) :
    RecyclerView.Adapter<FAQAdapter.ViewHolder>() {

    var isOpened = false

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPlus : ImageView = itemView.findViewById(R.id.imgPlus)
        var viewSeparator: View = itemView.findViewById(R.id.viewSeparator)
        var txtTitle : TextView = itemView.findViewById(R.id.txtTitle)
        var txtSubtitle : TextView = itemView.findViewById(R.id.txtSubtitle)
        var layoutSubHeader : RelativeLayout = itemView.findViewById(R.id.layoutSubHeader)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_faq_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = faqList?.get(position)
        holder.txtTitle.text = data?.faqTitle
        holder.txtSubtitle.text = data?.faqSubtitle

        holder.itemView.setOnClickListener {
            if (!isOpened) {
                isOpened = true
                setLayoutVisibility(holder, 8, 0, R.drawable.ic_minus_icon)
            } else {
                isOpened = false
                setLayoutVisibility(holder, 0, 8, R.drawable.ic_plus_sign)
            }
        }
    }

    private fun setLayoutVisibility(holder: ViewHolder, viewVisible: Int,
                                    layoutVisible: Int, drawable: Int) {
        holder.viewSeparator.visibility = viewVisible
        holder.layoutSubHeader.visibility = layoutVisible
        holder.imgPlus.setImageResource(drawable)
    }

    override fun getItemCount(): Int {
        return faqList?.size!!
    }
 }