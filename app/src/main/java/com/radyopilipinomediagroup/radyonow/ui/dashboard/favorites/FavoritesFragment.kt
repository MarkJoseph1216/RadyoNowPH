package com.radyopilipinomediagroup.radyonow.ui.dashboard.favorites

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyonow.R
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyonow.ui.dashboard.home.HomePresenter

class FavoritesFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>, FavoritesPresenter.View {

    private var favoritesRecycler: RecyclerView? = null
    private var faveView : View? = null
    private var presenter : FavoritesPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        faveView = inflater.inflate(R.layout.fragment_favorites, container, false)
        initInitialize()
        initMain()
        return faveView
    }

    private fun initInitialize() {
        favoritesRecycler = faveView?.findViewById(R.id.favoritesRecycler)
        presenter = FavoritesPresenter(this)
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


}