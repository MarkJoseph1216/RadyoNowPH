package com.radyopilipinomediagroup.radyonow.ui.login

import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter

class LoginPresenter(var view: LoginActivity) : AbstractPresenter<LoginActivity>(view) {
    fun doLogin(username: String, password: String, result: AbstractPresenter.ResultHandler) {
        if(username.isNullOrEmpty() && password.isNullOrEmpty())
            result.onError("Username and Password is empty")
        else
        {
            if(username == "radyouser" && password == "123456789"){
                result.onSuccess("Login Successful")
            }else{
                result.onError("Username and Password is incorrect.")

    interface View : AbstractPresenter.AbstractView {

    }
}