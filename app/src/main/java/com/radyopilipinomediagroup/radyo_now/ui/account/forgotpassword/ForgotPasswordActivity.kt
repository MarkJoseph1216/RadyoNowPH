package com.radyopilipinomediagroup.radyo_now.ui.account.forgotpassword

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.account.login.LoginActivity
import com.radyopilipinomediagroup.radyo_now.utils.Services

class ForgotPasswordActivity : AppCompatActivity(), AbstractPresenter.ContextView<ForgotPasswordActivity>, ForgotPasswordPresenter.View {

    private var forgotPassSubmit: Button? = null
    private var userEmail: TextView? = null
    private var btnCancel: Button? = null
    private var emailErr: TextView? = null
    private var txtMessage: TextView? = null
    private var linearLayoutMessage : LinearLayout? = null
    private var layoutForgotFields : LinearLayout? = null
    private var btnGotIt : Button? = null
    private var presenter: ForgotPasswordPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        Services.setActivityFullScreen(activity())
        initInitialize()
    }

    private fun initInitialize() {
        forgotPassSubmit = findViewById(R.id.forgotPassSubmit)
        emailErr = findViewById(R.id.emailErr)
        userEmail = findViewById(R.id.userEmail)
        btnCancel = findViewById(R.id.btnCancel)
        txtMessage = findViewById(R.id.txtMessage)
        linearLayoutMessage = findViewById(R.id.linearLayoutMessage)
        layoutForgotFields = findViewById(R.id.layoutForgotFields)
        btnGotIt = findViewById(R.id.btnGotIt)
        presenter = ForgotPasswordPresenter(this)
    }

     fun clickedListener(v: View?) {
         when(v?.id) {
             R.id.forgotPassSubmit -> presenter?.doForgotPassword(userEmail?.text.toString())
             R.id.btnCancel -> onBackPressed()
             R.id.btnGotIt -> onBackPressed()
         }
    }

    override fun onBackPressed() {
        Services.nextIntent(activity(), LoginActivity::class.java)
    }

    override fun activity(): ForgotPasswordActivity {
        return this
    }

    override fun context(): Context {
        return this
    }

    override fun applicationContext(): Context {
        return this.applicationContext
    }

    override fun getEmailErr(): TextView {
        return emailErr!!
    }

    override fun getTxtMessage(): TextView {
        return txtMessage!!
    }

    override fun getLayoutMessage(): LinearLayout {
        return linearLayoutMessage!!
    }

    override fun getLayoutFields(): LinearLayout {
        return layoutForgotFields!!
    }
}