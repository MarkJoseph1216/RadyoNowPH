package com.radyopilipinomediagroup.radyonow.repositories

import android.content.Context
import com.radyopilipinomediagroup.radyonow.model.LoginModel

class Repositories {

    fun getLogin() : LoginModel{
        return LoginModel("radyouser","12345")
    }
}