package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.terms

import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter

class TermsPresenter(var view: TermsFragment): AbstractPresenter<TermsFragment>(view) {

    fun setToolbarSetup(){
        getToolbarDrawerIcon!!.visibility = android.view.View.GONE
        getToolbarBack!!.visibility = android.view.View.GONE
        getToolbarDrawerIcon!!.visibility = android.view.View.VISIBLE
        getToolbarLogo!!.visibility = android.view.View.VISIBLE
    }

    interface View: AbstractView {}
}