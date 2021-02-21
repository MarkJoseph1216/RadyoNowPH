package com.radyopilipinomediagroup.radyonow.ui

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.radyopilipinomediagroup.radyonow.local.SessionManager
import com.radyopilipinomediagroup.radyonow.repositories.Repositories

abstract class AbstractPresenter<E>(view:E) where E : AbstractPresenter.ContextView<*>, E : AbstractPresenter.AbstractView {

    var getView: E? = null
        protected set
    var getRepositories : Repositories? = null
        protected set
    var getSessionManager : SessionManager? = null
        protected set

    init{
        getView = view
        getRepositories = Repositories()
        getSessionManager = SessionManager(view.applicationContext())
    }

    interface ResultHandler{
        fun onSuccess(message : String)
    interface OnResultListener{
        fun onSuccess(message: String)
    }
}