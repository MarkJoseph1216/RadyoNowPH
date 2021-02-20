package com.radyopilipinomediagroup.radyonow.ui.dashboard.profile

import androidx.fragment.app.FragmentActivity
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter

class ProfilePresenter(var view: ProfileFragment): AbstractPresenter<ProfileFragment>(view) {

    interface View : AbstractPresenter.AbstractView {

    }
}