package com.radyopilipinomediagroup.radyonow.ui.registration

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.radyopilipinomediagroup.radyonow.R
import com.radyopilipinomediagroup.radyonow.model.RegistrationModel
import com.radyopilipinomediagroup.radyonow.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyonow.ui.login.LoginActivity
import com.radyopilipinomediagroup.radyonow.utils.Services

class RegistrationActivity : AppCompatActivity(), RegistrationPresenter.View,
    AbstractPresenter.ContextView<RegistrationActivity>, View.OnClickListener{

    private var email : EditText? = null
    private var firstName : EditText? = null
    private var lastName : EditText? = null
    private var bDate : TextView? = null
    private var age : EditText? = null
    private var gender : EditText? = null
    private var city : EditText? = null
    private var region : EditText? = null
    private var password : EditText? = null
    private var confirmPassword : EditText? = null
    private var userRegister: Button? = null
    private var presenter : RegistrationPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        initDeclaration()
        initListener()
    }

    private fun initDeclaration() {
        email = findViewById(R.id.userEmail)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        bDate = findViewById(R.id.bDate)
        age = findViewById(R.id.age)
        gender = findViewById(R.id.gender)
        city = findViewById(R.id.city)
        region = findViewById(R.id.region)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirmPassword)
        userRegister = findViewById(R.id.userRegister)
        presenter = RegistrationPresenter(this)
    }

    private fun initListener() {
        userRegister?.setOnClickListener(this::onClick)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.userRegister -> {
                presenter?.onRegister(
                    RegistrationModel(
                        email?.text.toString(),
                        firstName?.text.toString(),
                        lastName?.text.toString(),
                        bDate?.text.toString(),
                        age?.text.toString(),
                        gender?.text.toString(),
                        city?.text.toString(),
                        region?.text.toString(),
                        password?.text.toString(),
                        userRegister?.text.toString()
                    ), object:AbstractPresenter.ResultHandler{
                        override fun onSuccess(message: String) {
                            Log.d("Registration", message)
                        }
                        override fun onError(message: String) {
                            Log.d("Registration_err", message)
                        }
                    }
                )
            }
        }
    }

    override fun onBackPressed() {
        Services.nextIntent(activity(), LoginActivity::class.java)
    }

    override fun activity(): RegistrationActivity {
        return this
    }

    override fun context(): Context {
        return this
    }

    override fun applicationContext(): Context {
        return applicationContext
    }
}
