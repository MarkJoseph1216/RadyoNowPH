package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.featured

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.stations.StationProgramResultModel
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs.details.ProgramDetailsFragment
import com.radyopilipinomediagroup.radyo_now.utils.Services
import java.lang.reflect.Field
import java.lang.reflect.Method

class ProgramListAdapter(var context : Context, var programs: List<StationProgramResultModel.Data>, var stationId: Int?) : RecyclerView.Adapter<ProgramListAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var programThumb: ImageView = itemView.findViewById(R.id.programThumb)
        var programTitle: TextView = itemView.findViewById(R.id.programTitle)
        var programDate: TextView = itemView.findViewById(R.id.programDate)
        var programsDesc: TextView = itemView.findViewById(R.id.programsDesc)
        var programFavorite: CheckBox = itemView.findViewById(R.id.programFavorite)
        var playProgram: ImageView = itemView.findViewById(R.id.playProgram)
        var otherOption: ImageView = itemView.findViewById(R.id.otherOption)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_channel_programs,parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return programs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val program = programs[position]

        try {
            holder.programTitle.text = program.name
            holder.programDate.text = if (program.contentsCount?.toInt() == 1) "${program.contentsCount} Episode" else "${program.contentsCount} Episodes"
            holder.programsDesc.text = program.description
            Glide.with(context)
                .load(program.thumbnail)
                .placeholder(R.drawable.ic_no_image)
                .into(holder.programThumb)
        } catch (e : Exception) {
            print(e.message)
        }
//        holder.programFavorite.isChecked = program.isUserFavorite!!
//        holder.playProgram.setOnClickListener {
//            if (program.format == "youtube"
//                || program.format == "video") {
//                setFragmentDetails(FeaturedStreamFragment(), stationId.toString(),
//                    program.id.toString(), program.name.toString(), program.type.toString(),"FeaturedStreamFragment")
//            } else {
//                setFragmentDetails(AudioStreamFragment(), stationId.toString(),
//                    program.id.toString(), program.name.toString(), program.type.toString(),"AudioStreamFragment")
//            }
//        }

        holder.otherOption.setOnClickListener {
            val wrapper = ContextThemeWrapper(
                context,
                R.style.popupMenu
            )
            val popup = PopupMenu(wrapper, it)
            popup.menuInflater?.inflate(R.menu.popup_menu, popup.menu)
            setForceShowIcon(popup)
            popup.setOnMenuItemClickListener{ menu ->
                when (menu.itemId) {
                    R.id.addToPlaylist -> {
                        (context as DashboardActivity).passData(programs[position].id!!)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.like -> {
                        Services.notAvailable(context)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.shareContent -> {
                        Services.notAvailable(context)
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }
            popup.show()
            }

        holder.itemView.setOnClickListener {
            setFragmentDetails(
                ProgramDetailsFragment(),
                program.id.toString(),
                "",
                "",
                "",
                "ProgramDetailsFragment"
            )
        }

//        holder.itemView.setOnClickListener {
//            if (program.format == "youtube"
//                || program.format == "video") {
//                setFragmentDetails(FeaturedStreamFragment(), stationId.toString(),
//                    program.id.toString(), program.name.toString(), program.type.toString(),"FeaturedStreamFragment")
//            } else {
//                setFragmentDetails(AudioStreamFragment(), stationId.toString(),
//                    program.id.toString(), program.name.toString(), program.type.toString(),"AudioStreamFragment")
//            }
//        }
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