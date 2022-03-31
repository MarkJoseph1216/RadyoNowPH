package com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs.details

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.ads.AdsModel
import com.radyopilipinomediagroup.radyo_now.model.programs.ProgramDetailsModel
import com.radyopilipinomediagroup.radyo_now.ui.AbstractInterface
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.favorites.FavoritesPresenter
import com.radyopilipinomediagroup.radyo_now.utils.Services

class ProgramDetailsFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>,
    ProgramDetailsPresenter.View, View.OnClickListener, AbstractInterface.AdsInterface, FavoritesPresenter.FavoriteCallback {

    private var programsView: View? = null
    private var programsRecycler: RecyclerView? = null
    private var programsThumb: ImageView? = null
    private var programDesc: TextView? = null
    private var programDescHeader: TextView? = null
    private var imgShowDesc: ImageView? = null
    var loadingPrograms: ProgressBar? = null
    var layoutSubtitleHolder: NestedScrollView? = null
    var params: RelativeLayout.LayoutParams? = null
    var imgBannerAds: ImageView? = null
    var imgOtherOption: ImageView? = null
    var programsTitle: TextView? = null
    var programEpisodes: TextView? = null
    var txtNoPrograms: TextView? = null
    var isShowed = false
    private var presenter: ProgramDetailsPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        programsView = inflater.inflate(R.layout.fragment_programs_details, container, false)
        initInitialize()
        initComponents()
        return programsView
    }

    private fun initInitialize() {
        layoutSubtitleHolder = programsView?.findViewById(R.id.layoutSubtitleHolder)
        programsRecycler = programsView?.findViewById(R.id.recyclerPrograms)
        loadingPrograms = programsView?.findViewById(R.id.loadingPrograms)
        programsTitle = programsView?.findViewById(R.id.programsTitle)
        programEpisodes = programsView?.findViewById(R.id.programEpisodes)
        programsThumb = programsView?.findViewById(R.id.programsThumb)
        programDesc = programsView?.findViewById(R.id.programDesc)
        imgBannerAds = programsView?.findViewById(R.id.imgBannerAds)
        programDescHeader = programsView?.findViewById(R.id.programDescHeader)
        imgOtherOption = programsView?.findViewById(R.id.imgOtherOption)
        txtNoPrograms = programsView?.findViewById(R.id.txtNoPrograms)
        imgShowDesc = programsView?.findViewById(R.id.imgShowDesc)
        presenter = ProgramDetailsPresenter(this)
    }
    private fun initComponents() {
        initClickListener()
        presenter?.getAds()
    }

    private fun initClickListener(){
        imgShowDesc?.setOnClickListener(this::onClick)
        imgOtherOption?.setOnClickListener(this::onClick)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layoutSubtitleHolder?.setOnScrollChangeListener(presenter?.onProgramsScrollChange)
        }
    }

    override fun activity(): FragmentActivity = activity!!
    override fun context(): Context = context!!
    override fun applicationContext(): Context = activity().applicationContext
    override fun getRecyclerView(): RecyclerView {
       return programsRecycler!!
    }

    @SuppressLint("SetTextI18n")
    override fun setProgramDetails(details: ProgramDetailsModel, episodeCount: String) {
        try {
            Glide.with(view!!.context)
                .load(details.data?.thumbnail)
                .placeholder(R.drawable.ic_no_image)
                .into(programsThumb!!)
            programsTitle?.text = details.data?.name
            programDesc?.text = details.data?.description
            programDescHeader?.text = details.data?.description
            imgShowDesc?.visibility = View.VISIBLE
            presenter?.getToolbarText?.text = ""
            presenter?.getToolbarLogo?.visibility = View.VISIBLE

            when(episodeCount) {
                "0" -> programEpisodes?.text = "$episodeCount Episode"
                else ->  programEpisodes?.text = "$episodeCount Episodes"
            }
        } catch (e: Exception) {}
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.imgShowDesc -> {
                if (!isShowed) {
                    isShowed = true
                    imgShowDesc!!.rotation = 180F
                    programDescHeader?.visibility = View.INVISIBLE
                    programDesc?.visibility = View.VISIBLE
                } else {
                    isShowed = false
                    imgShowDesc!!.rotation = 0F
                    programDescHeader?.visibility = View.VISIBLE
                    programDesc?.visibility = View.GONE
                }
            }

            R.id.imgOtherOption -> presenter?.showPopUpMenu()
        }
    }

    override fun onPopupReady(data: AdsModel.Data) {
        Services.popUpAds(context!!, data)
    }

    override fun onSliderReady(data: AdsModel.Data) {
    }

    override fun onBannerReady(data: AdsModel.Data) {
        when(data.assets[0].imageUrl.toString()) {
            "null" -> imgBannerAds?.visibility = View.GONE
            "" -> imgBannerAds?.visibility = View.GONE
            else -> {
                imgBannerAds?.setOnClickListener {
                    try {
                        presenter?.processDataAnalytics(data)
                    } catch (e: Exception) {}
                }
                Glide.with(presenter?.context!!).load(data.assets[0].imageUrl).into(imgBannerAds!!)
            }
        }
    }

    override fun deleteFavorite(contentId: String) {
        presenter?.deleteFavorite(contentId)
    }

    override fun addToFavorites(contentId: String) {
        presenter?.addToFavorites(contentId)
    }

    fun checkRestriction(buttonId: Int): Boolean? = presenter?.checkRestriction(buttonId)
}