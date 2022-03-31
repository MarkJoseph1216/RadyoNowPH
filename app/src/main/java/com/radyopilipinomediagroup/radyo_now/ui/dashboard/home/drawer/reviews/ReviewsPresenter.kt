package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.reviews

import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter

class ReviewsPresenter(var view: ReviewsFragment): AbstractPresenter<ReviewsFragment>(view) {

    fun setToolbarSettings() {
        getToolbarText!!.text = "Your Feedback"
        getToolbarBack!!.visibility = android.view.View.GONE
        getToolbarDrawerIcon!!.visibility = android.view.View.VISIBLE
    }

    interface View : AbstractPresenter.AbstractView {}
}