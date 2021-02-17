package com.radyopilipinomediagroup.radyonow.ui.registration

import com.google.gson.Gson
import com.radyopilipinomediagroup.radyonow.model.RegistrationModel
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter

class RegistrationPresenter(var view : AbstractPresenter.View) : RegistrationActivity.Presenter {

    override fun onRegister(userRegDetails: RegistrationModel) {
        view.onSuccess(Gson().toJson(userRegDetails))
    }

}