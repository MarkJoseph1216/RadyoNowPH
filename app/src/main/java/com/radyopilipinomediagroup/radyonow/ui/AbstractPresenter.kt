package com.radyopilipinomediagroup.radyonow.ui

import android.app.Activity
import android.content.Context
import com.radyopilipinomediagroup.radyonow.local.SessionManager
import com.radyopilipinomediagroup.radyonow.repositories.Repositories

abstract class AbstractPresenter<E>(protected var view: E) where E : AbstractPresenter.ContextView<*>?, E : AbstractPresenter.AbstractView? {

    private var repositories : Repositories? = null
    private var sessionManager : SessionManager? = null

    interface OnResultListener{
        fun onSuccess(message: String)
        fun onError(message: String)
    }

    init{
        this.view = view
        repositories = Repositories(view!!.applicationContext())
        sessionManager = SessionManager(view!!.applicationContext())
    }

    interface AbstractView
    interface ContextView<T : Activity> {
        fun activity():T
        fun context(): Context
        fun applicationContext():Context
    }

}