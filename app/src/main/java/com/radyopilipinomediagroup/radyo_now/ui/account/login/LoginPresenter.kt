package com.radyopilipinomediagroup.radyo_now.ui.account.login

import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.local.SessionManager
import com.radyopilipinomediagroup.radyo_now.model.google.GoogleAccessTokenResult
import com.radyopilipinomediagroup.radyo_now.model.security.LoginModel
import com.radyopilipinomediagroup.radyo_now.model.security.LoginResultModel
import com.radyopilipinomediagroup.radyo_now.model.security.SSOResultModel
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.account.addbirthdate.AddBirthDateActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.utils.Constants
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.Services.Companion.facebookSignOut
import com.radyopilipinomediagroup.radyo_now.utils.Services.Companion.gmailSignOut
import com.radyopilipinomediagroup.radyo_now.utils.toast
import java.lang.Exception

class LoginPresenter(var view: LoginActivity) : AbstractPresenter<LoginActivity>(view) {
    
    private lateinit var googleSignInClient : GoogleSignInClient
    private var TAG_NAME = "RadyoNow"
    private var TAG_NAME_ERROR = "RadyoNowError"
    private var loginManager = LoginManager.getInstance()
    private var gso: GoogleSignInOptions? = null
    private var googleApiClient : GoogleApiClient? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var firebaseToken: String? = ""
    var callBackManager : CallbackManager = CallbackManager.Factory.create()

    private var SSORetrofitHandler = object:RetrofitService.ResultHandler<SSOResultModel>{
        override fun onSuccess(data: SSOResultModel?) {
            Log.d("SSO_DATA", Gson().toJson(data))
            Log.d("SSO_SUCCESS", data?.data?.token!!)
            if (data.data?.dateOfBirth.toString() != "null" && data.data?.dateOfBirth.toString() != "") {
                try {
                    getSessionManager?.setData(SessionManager.SESSION_TOKEN, data.data?.token)

                    //Set the login status of the user
                    getSessionManager?.setData(SessionManager.SESSION_STATUS, "logged_in")
                    getSessionManager?.setLoginType("user")

                    Services.nextIntent(view.activity(), DashboardActivity::class.java)
                    view.activity().toast("Successfully Logged In!")
                } catch (e: Exception) { e.message }
            } else {
                Services.nextIntent(view.activity(), data.data?.token!!, AddBirthDateActivity::class.java)
            }
        }
        override fun onError(error: SSOResultModel?) {
            gmailSignOut(view.context())
            facebookSignOut()
            Toast.makeText(view.context(), error?.message, Toast.LENGTH_SHORT).show()
        }
        override fun onFailed(message: String) {
            Log.d("Retrofit_Err", message)
        }
    }
  
    fun doLogin(username: String, password: String) {
        validateErrors("", "", "")
        val userLogin = LoginModel(username, password)
        getRepositories?.login(userLogin, firebaseToken.toString(),
            object : RetrofitService.ResultHandler<LoginResultModel> {
            override fun onSuccess(data: LoginResultModel?) {
                view.setLoadingVisibility()
                Log.d("Login_Success", Gson().toJson(data))
                getSessionManager?.setData(SessionManager.SESSION_TOKEN, data?.data?.token)

                //Set the login status of the user
                getSessionManager?.setData(SessionManager.SESSION_STATUS, "logged_in")
                getSessionManager?.setLoginType("user")

                Services.nextIntent(view.activity(), DashboardActivity::class.java)
                view.activity().toast("Successfully Logged In!")
            }

            override fun onError(error: LoginResultModel?) {
                view.setLoadingVisibility()
                validateErrors(
                    error?.errors?.email?.get(0),
                    error?.errors?.password?.get(0),
                    error?.message
                )
            }

            override fun onFailed(message: String) {
                view.setLoadingVisibility()
                Log.d("Retrofit_Err", message)
                Toast.makeText(view.context(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun initGoogleSignIn(){
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constants.GOOGLE_CLIENT_ID)
            .requestServerAuthCode(Constants.GOOGLE_CLIENT_ID)
            .requestProfile()
            .requestEmail()
            .requestId()
            .build()

//        googleApiClient = GoogleApiClient.Builder(view.applicationContext())
//            .enableAutoManage(view.activity(), GoogleApiClient.OnConnectionFailedListener {
//                println("ERROR : ${it.errorMessage}")
//            })
//            .addApi(Auth.GOOGLE_SIGN_IN_API,gso!!)
//            .build()
        googleSignInClient = GoogleSignIn.getClient(view.activity(), gso!!)
        view.startActivityIntent(googleSignInClient)
    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val acct = GoogleSignIn.getLastSignedInAccount(view.activity())
            if (acct != null) {
                val personName = acct.displayName
                val accountToken: String? = acct.idToken
                Log.d("Session ID", gso?.logSessionId + " ")
                Log.d("USER_ACCOUNT", account?.serverAuthCode + " ")
                Log.d("ACCESS_TOKEN", "$accountToken ")
                getRepositories?.requestAccessToken(account?.serverAuthCode,object :RetrofitService.ResultHandler<GoogleAccessTokenResult>{
                    override fun onSuccess(data: GoogleAccessTokenResult?) {
                        try {
                            Log.d("ACCESS_TOKEN", data?.accessToken!!)
                            Log.d("FIREBASE_TOKEN", firebaseToken.toString())
                            getRepositories?.getSSOResult(
                                firebaseToken.toString(),
                                "google",
                                data.accessToken!!,
                                SSORetrofitHandler
                            )
                        } catch (e: Exception) {
                            gmailSignOut(view.context())
                        }
                    }
                    override fun onError(error: GoogleAccessTokenResult?) {

                    }
                    override fun onFailed(message: String) {
                        Toast.makeText(view.context(), message, Toast.LENGTH_SHORT).show()
                    }
                })

            }
        } catch (e: ApiException) {
            Log.w(
                TAG_NAME_ERROR,
                "signInResult:failed code= " + e.statusCode + " Error: " + e.status
            )
        }
    }

    fun initFacebookSignIn(){
        loginManager.logInWithReadPermissions(view.activity(), Constants.FACEBOOK_PERMISSIONS)
        loginManager.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val accessToken = result?.accessToken
                Log.d("Facebook_AccessToken", accessToken!!.userId)
                Log.d("Facebook_AccessToken", accessToken.token)
                try {
                    getRepositories?.getSSOResult(firebaseToken.toString(),
                        "facebook", accessToken.token, SSORetrofitHandler)
                } catch (e: Exception) {
                    e.message
                }
            }

            override fun onCancel() {}
            override fun onError(error: FacebookException?) {
                Toast.makeText(view.context(), error?.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun validateErrors(emailErr: String?, passwordErr: String?, message: String?){
        if (emailErr != null) {
            if(emailErr.isNotEmpty()){
                view.getEmailErrView().text = emailErr
                view.getEmailErrView().visibility = android.view.View.VISIBLE
            }
        }else view.getEmailErrView().visibility = android.view.View.GONE

        if (passwordErr != null) {
            if(passwordErr.isNotEmpty()){
                view.getPasswordErrView().text = passwordErr
                view.getPasswordErrView().visibility = android.view.View.VISIBLE
            }
        }else view.getPasswordErrView().visibility = android.view.View.GONE

        if (message != null) {
            if(message.isNotEmpty()){
               Toast.makeText(view.context(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun initFirebaseService(){
        try {
            //Getting the token from firebase messaging.
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (it.isComplete) firebaseToken = it.result.toString()
            }
        } catch (e: Exception) {}
    }

    fun guestSignIn() {
        view.showLoading()
        println("guestSignIn")
        getRepositories?.guestSignIn(
            object : RetrofitService.ResultHandler<LoginResultModel> {
                override fun onSuccess(data: LoginResultModel?) {
                    println("guestSignIn_success")
                    getSessionManager?.setData(SessionManager.SESSION_TOKEN, data?.data?.token)
                    //Set the login status of the user
                    getSessionManager?.setData(SessionManager.SESSION_STATUS, "logged_in")
                    getSessionManager?.setLoginType("guest")

                    view.setLoadingVisibility()
                    Services.nextIntent(view.activity(), DashboardActivity::class.java)
                }
                override fun onError(error: LoginResultModel?) {
                    println("guestSignIn_error $error")
                    view.setLoadingVisibility()
                }
                override fun onFailed(message: String) {
                    Log.d("Retrofit_Err", message)
                    Toast.makeText(view.context(), message, Toast.LENGTH_SHORT).show()
                    view.setLoadingVisibility()
                }
            }
        )
    }

    interface View : AbstractView {
        fun startActivityIntent(googleSignInClient: GoogleSignInClient)
        fun getEmailErrView() : TextView
        fun getPasswordErrView() : TextView
        fun setLoadingVisibility()
    }
}