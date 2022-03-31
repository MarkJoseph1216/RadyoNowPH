package com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.programs.ProgramsModel
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs.details.ProgramDetailsFragment
import com.radyopilipinomediagroup.radyo_now.utils.Services

class ProgramListAdapter(var context: Context, var programs: List<ProgramsModel.Data>) :
    RecyclerView.Adapter<ProgramListAdapter.ViewHolder>() {

    private var firebaseAnalytics: FirebaseAnalytics? = Firebase.analytics

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var programsThumb : ShapeableImageView = itemView.findViewById(R.id.programsThumb)
        var programsTitle : TextView = itemView.findViewById(R.id.programsTitle)
        var programsSubtitle : TextView = itemView.findViewById(R.id.programsSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_db_programs,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val program = programs[position]
        Glide.with(context)
            .load(program.thumbnail)
            .placeholder(R.drawable.ic_no_image)
            .into(holder.programsThumb)
        holder.programsTitle.text = program.name
        holder.programsSubtitle.text = if(program.contentsCount!! == 0
            || program.contentsCount!! == 1) "${program.contentsCount} Episode"
        else "${program.contentsCount} Episodes"

        holder.itemView.setOnClickListener {
            Services.setDataAnalytics(firebaseAnalytics,"program", program.name!!,"select_program")
            setFragmentDetails(
                ProgramDetailsFragment(),
                program.id.toString(),
                program.contentsCount.toString(),
                "",
                "",
                "ProgramDetailsFragment"
            )
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
        return programs.size
    }
 }