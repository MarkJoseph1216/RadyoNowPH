package com.radyopilipinomediagroup.radyonow.ui.login

import com.radyopilipinomediagroup.radyonow.model.LoginModel
import com.radyopilipinomediagroup.radyonow.repositories.Repositories
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter

class LoginPresenter(var view: LoginActivity) : AbstractPresenter<LoginActivity>(view) {
  
    private var userLogin : LoginModel? = getRepositories?.getLogin()
  
    fun doLogin(username: String, password: String, result: AbstractPresenter.ResultHandler) {
        if(username.isNullOrEmpty() && password.isNullOrEmpty())
            result.onError("Username and Password is empty")
        else {
            if(userLogin?.username.equals(username) && userLogin?.password.equals(password)){
                result.onSuccess("Login Successful")
            } else {
                result.onError("Username and Password is incorrect.")

    interface View : AbstractPresenter.AbstractView {

    }
}