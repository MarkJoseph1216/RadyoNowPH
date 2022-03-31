package com.radyopilipinomediagroup.radyo_now.ui.account.forgotpassword

import android.annotation.SuppressLint
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.model.security.ForgotPassResultModel
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter

class ForgotPasswordPresenter(var view : ForgotPasswordActivity): AbstractPresenter<ForgotPasswordActivity>(view) {

    fun doForgotPassword(email: String){
        validateErrors("", "")
        getRepositories?.forgotPassword(email, object:RetrofitService.ResultHandler<ForgotPassResultModel>{
            override fun onSuccess(data: ForgotPassResultModel?) {
                setMessageVisibility(email)
                Log.d("FORGOT_PASS", Gson().toJson(data))
            }
            override fun onError(error: ForgotPassResultModel?) {
                validateErrors(error?.errors?.email?.get(0), error?.message)
            }
            override fun onFailed(message: String) {
                Log.d("FORGOT_PASS_FAILED", message!!)
            }
        })
    }

    fun validateErrors(emailErr : String?, message: String?){
        if (emailErr != null) {
            if(emailErr.isNotEmpty()){
                view.getEmailErr().text = emailErr
                view.getEmailErr().visibility = android.view.View.VISIBLE
            }
        } else view.getEmailErr().visibility = android.view.View.GONE

        if (message != null) {
            if(message.isNotEmpty()){
                view.getEmailErr().text = message
                view.getEmailErr().visibility = android.view.View.VISIBLE
            }
        } else view.getEmailErr().visibility = android.view.View.GONE
    }

    @SuppressLint("SetTextI18n")
    fun setMessageVisibility(email: String){
        view.getLayoutFields().visibility = android.view.View.GONE
        view.getLayoutMessage().visibility = android.view.View.VISIBLE
        view.getTxtMessage().text = """
        An email has been sent to $email with further instructions.
        """
    }

    interface View : AbstractPresenter.AbstractView{
        fun getEmailErr() : TextView
        fun getTxtMessage() : TextView
        fun getLayoutMessage() : LinearLayout
        fun getLayoutFields() : LinearLayout
    }
}