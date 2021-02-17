package com.radyopilipinomediagroup.radyonow.ui.login

class LoginPresenter(var view: LoginActivity) : LoginActivity.Presenter {


    override fun doLogin(username: String, password: String) {
        if(username.isNullOrEmpty() && password.isNullOrEmpty())
            view.onError("Username and Password is empty")
        else
        {
            if(username == "radyouser" && password == "123456789"){
                view.onSuccess("Login Successful")
            }else{
                view.onError("Username and Password is incorrect.")
            }
        }
    }

}


