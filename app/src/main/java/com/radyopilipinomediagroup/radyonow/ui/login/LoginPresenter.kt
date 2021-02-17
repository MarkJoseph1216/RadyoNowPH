package com.radyopilipinomediagroup.radyonow.ui.login

import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter

class LoginPresenter : AbstractPresenter<LoginActivity> {

    constructor(_view: LoginActivity) : super(_view) {

    }

    fun doLogin(username: String, password: String) {
        if(username.isNullOrEmpty() && password.isNullOrEmpty())
//            result.onError("Username and Password is empty")
        else
        {
            if(username == "radyouser" && password == "123456789"){
//                result.onSuccess("Login Successful")
            }else{
//                result.onError("Username and Password is incorrect.")
            }
        }
    }

    interface View : AbstractPresenter.AbstractView {

    }

}

