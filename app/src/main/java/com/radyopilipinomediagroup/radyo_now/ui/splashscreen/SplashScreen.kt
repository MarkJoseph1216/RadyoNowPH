package com.radyopilipinomediagroup.radyo_now.ui.splashscreen

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter

class SplashScreen : AppCompatActivity(), AbstractPresenter.ContextView<SplashScreen>,
    SplashPresenter.View {

    private var presenter : SplashPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        initComponents()
    }



    fun initComponents(){
        presenter = SplashPresenter(this)
        presenter?.handleIntentData()
    }

    override fun activity(): SplashScreen {
        return this
    }

    override fun context(): Context {
        return this
    }

    override fun applicationContext(): Context {
        return applicationContext
    }
}