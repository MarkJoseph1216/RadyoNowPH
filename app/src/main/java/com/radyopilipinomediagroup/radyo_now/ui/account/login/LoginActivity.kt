package com.radyopilipinomediagroup.radyo_now.ui.account.login

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.textfield.TextInputEditText
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.account.forgotpassword.ForgotPasswordActivity
import com.radyopilipinomediagroup.radyo_now.ui.account.registration.RegistrationActivity
import com.radyopilipinomediagroup.radyo_now.utils.Constants
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.guestWarningDialog
import com.wang.avi.AVLoadingIndicatorView


class LoginActivity : AppCompatActivity(), LoginPresenter.View,
    AbstractPresenter.ContextView<LoginActivity>, View.OnClickListener {

    private var userEmail : EditText? = null
    private var userPassword : TextInputEditText? = null

    private var emailErr : TextView? = null
    private var passwordErr : TextView? = null
    private var txtSignUp: TextView? = null
    private var textGuest: TextView? = null

    private var imgFacebook : ImageView? = null
    private var imgGmail : ImageView? = null

    private var userLogin : Button? = null
    private var presenter : LoginPresenter? = null
    private var toRegister: ViewGroup? = null
    private var asGuest: ViewGroup? = null

    private var txtForgotPassword : TextView? = null
    private var loadingLogin : AVLoadingIndicatorView? = null
    private var layoutLogin : RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Services.setActivityFullScreen(activity())
        initDeclaration()
        initComponents()
    }

    private fun initComponents() {
        initListener()
    }

    private fun initDeclaration() {
        textGuest = findViewById(R.id.textGuest)
        asGuest = findViewById(R.id.asGuest)
        emailErr = findViewById(R.id.emailErr)
        passwordErr = findViewById(R.id.passwordErr)
        userEmail = findViewById(R.id.userEmail)
        userPassword = findViewById(R.id.userPassword)
        imgFacebook = findViewById(R.id.imgFacebook)
        imgGmail = findViewById(R.id.imgGmail)
        userLogin  = findViewById(R.id.userLogin)
        loadingLogin = findViewById(R.id.loginLoading)
        layoutLogin = findViewById(R.id.layoutLoading)
        toRegister = findViewById(R.id.toRegister)
        txtSignUp = findViewById(R.id.txtSignUp)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        presenter = LoginPresenter(this)
        presenter?.initFirebaseService()

        txtSignUp?.paintFlags = txtSignUp?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)!!
        textGuest?.paintFlags = textGuest?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)!!
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun initListener(){
        asGuest?.setOnClickListener(activity()::onClick)
        userLogin?.setOnClickListener(activity()::onClick)
        toRegister?.setOnClickListener(activity()::onClick)
        imgFacebook?.setOnClickListener(activity()::onClick)
        imgGmail?.setOnClickListener(activity()::onClick)
        txtForgotPassword?.setOnClickListener(activity()::onClick)
    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.asGuest -> presenter?.guestSignIn()
            R.id.userLogin -> {
                showLoading()
                presenter?.doLogin(
                    userEmail?.text.toString(),
                    userPassword?.text.toString()
                )
            }
            R.id.toRegister -> Services.nextIntent(this, RegistrationActivity::class.java)
            R.id.imgFacebook -> {
                presenter?.initFacebookSignIn()
            }
            R.id.imgGmail -> {
                presenter?.initGoogleSignIn()
            }
            R.id.txtForgotPassword -> Services.nextIntent(
                this,
                ForgotPasswordActivity::class.java
            )
        }
    }

    fun showLoading() {
        userLogin!!.visibility = View.GONE
        layoutLogin!!.visibility = View.VISIBLE
        loadingLogin!!.visibility = View.VISIBLE
        loadingLogin!!.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter?.callBackManager?.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == Constants.GOOGLE_SIGN_IN_CODE) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                presenter?.handleSignInResult(task)
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
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

    override fun startActivityIntent(googleSignInClient: GoogleSignInClient) {
//        val intent = Auth.GoogleSignInApi.getSignInIntent(googleSignInClient)

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Constants.GOOGLE_SIGN_IN_CODE)
    }

    override fun getEmailErrView(): TextView {
        return emailErr!!
    }

    override fun getPasswordErrView(): TextView {
        return passwordErr!!
    }

    override fun setLoadingVisibility() {
        layoutLogin!!.visibility = View.GONE
        loadingLogin!!.visibility = View.GONE
        loadingLogin!!.hide()
        userLogin!!.visibility = View.VISIBLE
    }
}