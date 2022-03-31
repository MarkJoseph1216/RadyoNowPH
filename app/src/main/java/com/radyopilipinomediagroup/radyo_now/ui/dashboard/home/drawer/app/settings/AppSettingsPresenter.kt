package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.app.settings

import com.radyopilipinomediagroup.radyo_now.local.SessionManager
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter

class AppSettingsPresenter(var view: AppSettingsFragment): AbstractPresenter<AppSettingsFragment>(view) {


    var notifSettings = getSessionManager?.getSessionSettings()

    init {
        setupToolbar()
    }

    private fun setupToolbar() {
        getToolbarDrawerIcon?.visibility = android.view.View.VISIBLE
        getToolbarBack?.visibility = android.view.View.GONE
        getToolbarLogo?.visibility = android.view.View.VISIBLE
        getToolbarText?.text = "Settings"
    }

    fun changeNotifSettings(checked: Boolean) {
        getSessionManager?.setData(SessionManager.SESSION_SETTINGS, checked)
    }

    interface View: AbstractPresenter.AbstractView{

    }
}