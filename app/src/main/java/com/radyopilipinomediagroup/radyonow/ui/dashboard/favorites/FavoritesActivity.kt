package com.radyopilipinomediagroup.radyonow.ui.dashboard.favorites

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.radyopilipinomediagroup.radyonow.R
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter

class FavoritesActivity : AppCompatActivity(), AbstractPresenter.ContextView<FavoritesActivity>, FavoritesPresenter.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
    }

    override fun activity(): FavoritesActivity {
        return this
    }

    override fun context(): Context {
        return this
    }

    override fun applicationContext(): Context {
        return applicationContext
    }
}