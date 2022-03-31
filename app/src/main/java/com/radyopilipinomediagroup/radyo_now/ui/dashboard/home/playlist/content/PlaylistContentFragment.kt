package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.playlist.content

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistListResultModel
import com.radyopilipinomediagroup.radyo_now.ui.AbstractInterface
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs.ProgramsFragment
import com.radyopilipinomediagroup.radyo_now.utils.Services
import io.supercharge.shimmerlayout.ShimmerLayout

class PlaylistContentFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>, PlaylistContentPresenter.View,
    AbstractInterface.DataHandler<PlaylistListResultModel.Data>, AbstractInterface.PlaylistContentSettings {

    private var contentView: View? = null
    private var playlistName: TextView? = null
    private var playlistContentRecycler: RecyclerView? = null
    private var presenter: PlaylistContentPresenter? = null
    private var stationData: PlaylistListResultModel.Data? = null
    private var shimmerLayoutAllPlayList : ShimmerLayout? = null
    private var noContent: RelativeLayout? = null
    private var browseContent: TextView? = null
    private var defTypeface: Typeface? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.fragment_playlist_content, container, false)
        initInitialize()
        initComponents()
        return contentView
    }

    private fun initInitialize() {
        noContent = contentView?.findViewById(R.id.noContent)
        browseContent = contentView?.findViewById(R.id.browseContent)
        playlistName = contentView?.findViewById(R.id.playlistName)
        playlistContentRecycler = contentView?.findViewById(R.id.playlistContentRecycler)
        shimmerLayoutAllPlayList = contentView?.findViewById(R.id.shimmerLayoutAllPlayList)
        presenter = PlaylistContentPresenter(this)
    }

    @SuppressLint("SetTextI18n")
    private fun initComponents() {
        println("initComponents: asdasdasdasd")
//        defTypeface = presenter?.getToolbarText?.typeface
//        presenter?.getToolbarText?.typeface = ResourcesCompat.getFont(context!!, R.font.metropolis_medium)

//        playlistName?.text = "| ${stationData?.name}"
        initListeners()
    }

//    override fun onStop() {
//        presenter?.getToolbarText?.typeface = defTypeface
//        super.onStop()
//    }
//
//    override fun onDestroy() {
//        presenter?.getToolbarText?.typeface = defTypeface
//        super.onDestroy()
//    }
//
//    override fun onPause() {
//        presenter?.getToolbarText?.typeface = defTypeface
//        super.onPause()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        presenter?.getToolbarText?.typeface = ResourcesCompat.getFont(context!!, R.font.metropolis_medium)
//    }
//
//    override fun onStart() {
//        super.onStart()
//        presenter?.getToolbarText?.typeface = ResourcesCompat.getFont(context!!, R.font.metropolis_medium)
//    }

    private fun initListeners() {
        browseContent?.setOnClickListener {
            Services.changeFragment(activity?.supportFragmentManager!!, ProgramsFragment(),"ProgramsFragment" )
        }
    }

    override fun activity(): FragmentActivity {
        return activity!!
    }

    override fun context(): Context {
        return context!!
    }

    override fun applicationContext(): Context {
        return activity().applicationContext!!
    }

    override fun passData(data: PlaylistListResultModel.Data) {
        println("passData_content: ${Gson().toJson(data)}")
        stationData = data
//        presenter?.updateContentData()
    }

    override fun getBackStacked() { }
    override fun getRecycler(): RecyclerView = playlistContentRecycler!!
    override fun getShimmerLayout(): ShimmerLayout = shimmerLayoutAllPlayList!!
    override fun getStationData(): PlaylistListResultModel.Data = stationData!!
    override fun getNoContent(): RelativeLayout  = noContent!!

    override fun onRemoveContent(contentId: Int) {
        presenter?.removeContent(contentId)
    }

    fun checkRestriction(buttonId: Int): Boolean? = presenter?.checkRestrictions(buttonId)
}