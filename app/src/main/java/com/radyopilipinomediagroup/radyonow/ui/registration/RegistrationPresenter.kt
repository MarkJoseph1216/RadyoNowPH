package com.radyopilipinomediagroup.radyonow.ui.registration

import com.google.gson.Gson
import com.radyopilipinomediagroup.radyonow.model.RegistrationModel
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter

class RegistrationPresenter(var view : RegistrationActivity) : AbstractPresenter<RegistrationActivity>(view) {

    fun onRegister(userRegDetails: RegistrationModel, result : AbstractPresenter.ResultHandler) {
        result.onSuccess(Gson().toJson(userRegDetails))
    }

    interface View : AbstractPresenter.AbstractView{

    }
}