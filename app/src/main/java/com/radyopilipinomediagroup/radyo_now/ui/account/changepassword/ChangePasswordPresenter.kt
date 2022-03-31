package com.radyopilipinomediagroup.radyo_now.ui.account.changepassword

import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.model.security.ChangePasswordResponse
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter

class ChangePasswordPresenter(var view : ChangePasswordActivity): AbstractPresenter<ChangePasswordActivity>(view) {

    fun changePassword(password: String, newPassword: String, confirmNewPassword: String){
        println("changePassword: ${view.getEmail()}")
        getRepositories?.changePassword(view.getEmail(),password, newPassword, confirmNewPassword, getSessionManager?.getToken()!!, object:
            RetrofitService.ResultHandler<ChangePasswordResponse>{
            override fun onSuccess(data: ChangePasswordResponse?) {
                Toast.makeText(view.activity(),"Password Successfully Changed!", Toast.LENGTH_SHORT).show()
                Log.d("CHANGE_PASS", Gson().toJson(data))
                view.resetDetails()
            }
            override fun onError(error: ChangePasswordResponse?) {
                validateErrors(error?.errors?.password?.get(0), error?.errors?.new_password?.get(0), error?.errors?.new_password_confirmation?.get(0), error?.message)
                Log.d("CHANGE_ERROR", error?.message.toString())
            }
            override fun onFailed(message: String) {
                Log.d("CHANGE_PASS_FAILED", message!!)
                Toast.makeText(view.activity(),"Something wen't wrong, Please Try Again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun validateErrors(oldPassErr : String?, newPassErr : String?, confirmPassErr : String?, message: String?){
        if (oldPassErr != null) {
            if(oldPassErr.isNotEmpty()){
                view.getOldPassErr().text = oldPassErr
                view.getOldPassErr().visibility = android.view.View.VISIBLE
            }
        } else view.getOldPassErr().visibility = android.view.View.GONE

        if (newPassErr != null) {
            if(newPassErr.isNotEmpty()){
                view.getNewPassErr().text = newPassErr
                view.getNewPassErr().visibility = android.view.View.VISIBLE
            }
        } else view.getOldPassErr().visibility = android.view.View.GONE

        if (confirmPassErr != null) {
            if(confirmPassErr.isNotEmpty()){
                view.getConfirmPassErr().text = confirmPassErr
                view.getConfirmPassErr().visibility = android.view.View.VISIBLE
            }
        } else view.getConfirmPassErr().visibility = android.view.View.GONE

        if (message != null) {
            if(message.isNotEmpty()){
                Toast.makeText(view.activity(), message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    interface View : AbstractPresenter.AbstractView{
        fun resetDetails()
        fun getOldPassErr() : TextView
        fun getNewPassErr() : TextView
        fun getConfirmPassErr() : TextView
        fun getEmail(): String
    }
}