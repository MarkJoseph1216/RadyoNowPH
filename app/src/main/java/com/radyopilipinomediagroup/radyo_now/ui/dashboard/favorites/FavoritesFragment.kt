package com.radyopilipinomediagroup.radyo_now.ui.dashboard.favorites

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.programs.ProgramsFragment
import com.radyopilipinomediagroup.radyo_now.utils.Services

class FavoritesFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>, FavoritesPresenter.View,
FavoritesPresenter.FavoriteCallback, View.OnClickListener{

    private var favoritesRecycler: RecyclerView? = null
    private var faveView : View? = null
    private var presenter : FavoritesPresenter? = null
    private var browseContent: TextView? = null
    private var loadingProgress: ProgressBar? = null
    private var noContent: RelativeLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        faveView = inflater.inflate(R.layout.fragment_favorites, container, false)
        initInitialize()
        initMain()
        initListener()
        return faveView
    }

    private fun initInitialize() {
        browseContent = faveView?.findViewById(R.id.browseContent)
        noContent = faveView?.findViewById(R.id.noContent)
        loadingProgress = faveView?.findViewById(R.id.loadingProgress)
        favoritesRecycler = faveView?.findViewById(R.id.favoritesRecycler)
        presenter = FavoritesPresenter(this)
    }

    private fun initListener(){
        browseContent?.setOnClickListener(this::onClick)
    }

    private fun initMain() {
        presenter?.displayFavorites()
    }

    override fun activity(): FragmentActivity {
        return activity!!
    }

    override fun context(): Context {
        return context!!
    }

    override fun applicationContext(): Context {
        return activity().applicationContext
    }

    override fun getRecycler(): RecyclerView {
        return favoritesRecycler!!
    }

    override fun setNoContentVisible(visible: Int, loadingVisible: Int) {
        noContent?.visibility = visible
        loadingProgress?.visibility = loadingVisible
    }

    override fun deleteFavorite(contentId: String) {
        presenter?.deleteFavorite(contentId)
    }

    override fun addToFavorites(contentId: String) {}

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.browseContent -> {
                (view.context as DashboardActivity).setBottomNav(R.id.programs)
                Services.changeFragment(activity?.supportFragmentManager!!, ProgramsFragment(),"ProgramsFragment")
            }
        }
    }

    fun checkRestriction(buttonId: Int): Boolean? = presenter?.checkRestriction(buttonId)
}