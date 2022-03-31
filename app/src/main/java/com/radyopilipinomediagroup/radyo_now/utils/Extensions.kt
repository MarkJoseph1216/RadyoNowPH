package com.radyopilipinomediagroup.radyo_now.utils

import android.app.Activity
import com.radyopilipinomediagroup.radyo_now.R
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.radyopilipinomediagroup.radyo_now.local.SessionManager
import com.radyopilipinomediagroup.radyo_now.ui.account.login.LoginActivity
import com.radyopilipinomediagroup.radyo_now.ui.account.registration.RegistrationActivity

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.guestWarningDialog(){
    val dialog = Dialog(this, android.R.style.Theme_Material_NoActionBar_Fullscreen)
    dialog.window?.setBackgroundDrawableResource(R.color.gray50)
    dialog.setContentView(R.layout.custom_dialog_alert)

    val btnCancel: TextView = dialog.findViewById(R.id.btnCancel)
    val btnSignIn: TextView = dialog.findViewById(R.id.btnSignIn)
    val btnSignUp: TextView = dialog.findViewById(R.id.btnSignUp)

    val signIn = View.OnClickListener{
        val sessionManager = SessionManager(this)
        sessionManager.setData(SessionManager.SESSION_STATUS, "logged_out")
        sessionManager.setLoginType("")
        when(it?.id){
            R.id.btnSignUp -> Services.nextIntent(this as Activity, RegistrationActivity::class.java)
            R.id.btnSignIn -> Services.nextIntent(this as Activity, LoginActivity::class.java)
        }
    }

    btnCancel.setOnClickListener{dialog.dismiss()}
    btnSignIn.setOnClickListener(signIn)
    btnSignUp.setOnClickListener(signIn)

    dialog.show()
}

fun log(msg: String) {
    Log.i("RadyoNowPH: ", msg)
}

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }

fun Fragment.showKeyboard() {
    val inputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun AppCompatActivity.showKeyboard() {
    val inputMethodManager = this!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun Fragment.hideKeyboard() {
    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(requireView().windowToken, 0)
}

fun AppCompatActivity.hideKeyboard() {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.window.attributes.token, 0)
}