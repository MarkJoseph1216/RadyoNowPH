package com.radyopilipinomediagroup.radyo_now.ui.splashscreen

import android.net.Uri
import android.os.Handler
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.account.login.LoginActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.utils.Services

class SplashPresenter(var view: SplashScreen) : AbstractPresenter<SplashScreen>(view) {


    private fun getLoggedStatus(){
        try {
            if (getSessionManager?.getSessionStatus()!! == "logged_in") {
                openActivityPage(DashboardActivity::class.java)
            } else {
                openActivityPage(LoginActivity::class.java)
            }
        } catch (e: Exception) {
            openActivityPage(LoginActivity::class.java)
        }
    }

    private fun openActivityPage(className: Class<*>) {
        val handler = Handler()
        handler.postDelayed({
            Services.nextIntent(view.activity(), className)
            view.activity().finish()
        }, 2000)
    }

    fun handleIntentData() {
        val data: Uri? = view.intent.data
        if (data != null && data.isHierarchical) {
            val uri: String? = view.intent.dataString
            val host = data.host
            println("handleIntentData: $host")
            openActivityPage(LoginActivity::class.java)
        }else getLoggedStatus()
    }

    interface View : AbstractView{}
}