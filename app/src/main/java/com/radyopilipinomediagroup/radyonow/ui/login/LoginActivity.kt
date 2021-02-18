package com.radyopilipinomediagroup.radyonow.ui.login

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.radyopilipinomediagroup.radyonow.R
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyonow.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyonow.ui.registration.RegistrationActivity
import com.radyopilipinomediagroup.radyonow.utils.Services

class LoginActivity : AppCompatActivity(), LoginPresenter.View,
    AbstractPresenter.ContextView<LoginActivity>, View.OnClickListener {

    private var userEmail : EditText? = null
    private var userPassword : EditText? = null
    private var userLogin : Button? = null
    private var presenter : LoginPresenter? = null
    private var toRegister: ViewGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initDeclaration()
        initListener()
    }

    private fun initDeclaration() {
        presenter = LoginPresenter(this)
        userEmail = findViewById(R.id.userEmail)
        userPassword = findViewById(R.id.userPassword)
        userLogin  = findViewById(R.id.userLogin)
        toRegister = findViewById(R.id.toRegister)
    }

    private fun initListener(){
        userLogin?.setOnClickListener(this::onClick)
        toRegister?.setOnClickListener(this::onClick)
    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.userLogin -> {
                presenter?.doLogin(userEmail?.text.toString(), userPassword?.text.toString(), object:AbstractPresenter.ResultHandler{
                    override fun onSuccess(message: String) {
                        Toast.makeText(context(), message, Toast.LENGTH_SHORT).show()
                        Services.nextIntent(activity(), DashboardActivity::class.java)
                    }
                    override fun onError(message: String) {
                        Toast.makeText(context(), message, Toast.LENGTH_SHORT).show()
                    }
                })
            }
            R.id.toRegister -> Services.nextIntent(this, RegistrationActivity::class.java)
        }
    }

    override fun activity(): LoginActivity {
        return this
    }

    override fun context(): Context {
        return this
    }

    override fun applicationContext(): Context {
        return applicationContext
    }

}

