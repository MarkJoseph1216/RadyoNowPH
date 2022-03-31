package com.radyopilipinomediagroup.radyo_now.ui.account.changepassword

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter

class ChangePasswordActivity : AppCompatActivity(), AbstractPresenter.ContextView<ChangePasswordActivity>, ChangePasswordPresenter.View {

    private var edtOldPassword : TextInputEditText? = null
    private var edtNewPassword : TextInputEditText? = null
    private var edtConfirmNewPassword : TextInputEditText? = null
    private var btnChangePassword : Button? = null
    private var btnCancel: Button? = null
    private var presenter: ChangePasswordPresenter? = null

    private var oldPassErr: TextView? = null
    private var newPassErr: TextView? = null
    private var confirmPassErr: TextView? = null
    private var email: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        initComponents()
        initOnClickListener()
    }

    fun initComponents() {
        edtOldPassword = findViewById(R.id.edtOldPassword)
        edtNewPassword = findViewById(R.id.edtNewPassword)
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        btnCancel = findViewById(R.id.btnCancel)

        oldPassErr = findViewById(R.id.oldPassErr)
        newPassErr = findViewById(R.id.newPassErr)
        confirmPassErr = findViewById(R.id.confirmPassErr)
        email = intent.extras?.getString("email")
        presenter = ChangePasswordPresenter(this)
    }

    private fun initOnClickListener() {
        btnChangePassword!!.setOnClickListener{ presenter?.changePassword(edtOldPassword!!.text.toString(),
            edtNewPassword!!.text.toString(), edtConfirmNewPassword!!.text.toString()) }
        btnCancel?.setOnClickListener { finish() }
    }

    override fun activity(): ChangePasswordActivity {
        return this
    }

    override fun context(): Context {
        return this
    }

    override fun applicationContext(): Context {
        return this.applicationContext
    }

    override fun resetDetails() {
        oldPassErr!!.text = ""
        newPassErr!!.text = ""
        confirmPassErr!!.text = ""
        finish()
    }

    override fun getOldPassErr(): TextView {
        return oldPassErr!!
    }

    override fun getNewPassErr(): TextView {
        return newPassErr!!
    }

    override fun getConfirmPassErr(): TextView {
        return confirmPassErr!!
    }

    override fun getEmail(): String = email!!

}