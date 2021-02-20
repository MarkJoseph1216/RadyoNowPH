package com.radyopilipinomediagroup.radyonow.ui.dashboard.favorites

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyonow.ui.StreamListAdapter

class FavoritesPresenter(var view : FavoritesFragment) : AbstractPresenter<FavoritesFragment>(view) {

    private var favorites = getRepositories?.getFavorites()
    private var adapter = StreamListAdapter(view.context(), favorites!!)
    private var llm = LinearLayoutManager(view.context())

    fun displayFavorites(){
        view.getRecycler().adapter = adapter
        view.getRecycler().layoutManager = llm
    }

    interface View : AbstractPresenter.AbstractView{
        fun getRecycler() : RecyclerView
    }
}