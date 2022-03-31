package com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.realm.RecentSearch
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs.search.ProgramSearchPresenter
import io.realm.RealmResults

class ProgramRecentAdapter(var context: Context, var recentList: RealmResults<RecentSearch>) :
    RecyclerView.Adapter<ProgramRecentAdapter.ViewHolder>() {

    private var interfaces = (context as FragmentActivity).supportFragmentManager.
    findFragmentByTag("ProgramSearchFragment") as ProgramSearchPresenter.ProgramInterface

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgClose : ImageView = itemView.findViewById(R.id.imgClose)
        var txtProgramName : TextView = itemView.findViewById(R.id.txtProgramName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_program_recent,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recentSearch = recentList[position]

        holder.txtProgramName.text = recentSearch?.programSearch
        holder.imgClose.setOnClickListener {
            try {
                interfaces.deleteRecentSearch(position)
            } catch (e: Exception) {}
        }

        holder.itemView.setOnClickListener {
            try {
                interfaces.searchProgram(recentSearch?.programSearch!!)
            } catch (e: Exception) {}
        }
    }

    override fun getItemCount(): Int {
        return recentList.size
    }
}