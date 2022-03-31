package com.radyopilipinomediagroup.radyo_now.ui

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.material.appbar.AppBarLayout
import com.mikhaellopez.circularimageview.CircularImageView
import com.radyopilipinomediagroup.radyo_now.local.SessionManager
import com.radyopilipinomediagroup.radyo_now.repositories.Repositories
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.utils.Services

abstract class AbstractPresenter<E>(view:E) where E : AbstractPresenter.ContextView<*>, E : AbstractPresenter.AbstractView {

    var getView: E? = null
        protected set
    var getRepositories : Repositories? = null
        protected set
    var getSessionManager : SessionManager? = null
        protected set
    var getToolbarDrawerIcon : ImageView? = null
        protected set
    var getToolbarText : TextView? = null
        protected set
    var fragmentOnClickListener : View.OnClickListener? = null
        protected set
    var getToolbarBack : ImageView? = null
        protected set
    var getToolbarLogo : ImageView? = null
        protected set
    var getToolbarClose : CircularImageView? = null
        protected set
    var getToolbarSearch : EditText? = null
        protected set
    var getToolbarLayoutSearch : RelativeLayout? = null
        protected set
    var getToolbarMain : RelativeLayout? = null
        protected set
    var getAppBarLayout : AppBarLayout? = null
        protected set

    init{
        getView = view
        getRepositories = Repositories()
        getSessionManager = SessionManager(view.applicationContext())
        getToolbarDrawerIcon = Services.getToolbarDrawerIcon(view.activity())
        getToolbarText = Services.getToolbarText(view.activity())
        getToolbarBack = Services.getToolbarBack(view.activity())
        getToolbarLogo = Services.getToolbarLogo(view.activity())
        getToolbarSearch = Services.getToolbarSearch(view.activity())
        getToolbarClose = Services.getToolbarClose(view.activity())
        getToolbarMain = Services.getToolbarMainHolder(view.activity())
        getToolbarLayoutSearch = Services.getToolbarLayoutSearch(view.activity())
        fragmentOnClickListener = Services.fragmentBackOnClick(view.activity())
        getAppBarLayout = Services.getAppBarLayout(view.activity())
        getToolbarBack?.setOnClickListener {
            (it.context as DashboardActivity).getBackStacked()
        }
    }

    fun toolbarMainHide(){
        getToolbarMain?.visibility = View.GONE
    }
    fun toolbarMainShow(){
        getToolbarMain?.visibility = View.VISIBLE
    }
    fun appBarHide(){
        getAppBarLayout?.visibility = View.GONE
    }
    fun appBarShow(){
        getAppBarLayout?.visibility = View.VISIBLE
    }

    interface ResultHandler {
        fun onSuccess(message: String)
        fun onError(message: String)
        interface OnResultListener {
            fun onSuccess(message: String)
        }
    }

    interface AbstractView
    interface ContextView<T : Activity> {
        fun activity():T
        fun context(): Context
        fun applicationContext():Context
    }
}