package com.radyopilipinomediagroup.radyo_now.ui.account.registration

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.security.RegistrationModel
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.account.login.LoginActivity
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.toast
import com.wang.avi.AVLoadingIndicatorView
import java.util.*


class RegistrationActivity : AppCompatActivity(), RegistrationPresenter.View,
    AbstractPresenter.ContextView<RegistrationActivity>, View.OnClickListener{

    private var email : EditText? = null
    private var firstName : EditText? = null
    private var lastName : EditText? = null
    private var bDate : TextView? = null
    private var gender : RadioGroup? = null
    private var genderButton : RadioButton? = null
    private var city : EditText? = null
    private var region : EditText? = null
    private var password : EditText? = null
    private var confirmPassword : EditText? = null
    private var txtAgreeTerms : TextView? = null
    private var toLogin : ViewGroup? = null

    private var emailErr : TextView? = null
    private var firstNameErr : TextView? = null
    private var lastNameErr : TextView? = null
    private var bDateErr : TextView? = null
    private var genderErr : TextView? = null
    private var cityErr : TextView? = null
    private var regionErr : TextView? = null
    private var passwordErr : TextView? = null

    private var checkBox : CheckBox? = null
    private var userRegister: Button? = null
    private var presenter : RegistrationPresenter? = null

    private var layoutSignupFields : LinearLayout? = null
    private var linearLayoutMessage : LinearLayout? = null
    private var layoutLoading : RelativeLayout? = null
    private var signUpLoading : AVLoadingIndicatorView? = null
    private var btnGotIt : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        Services.setActivityFullScreen(activity())
        initDeclaration()
        initListener()
    }

    private fun initDeclaration() {
        toLogin = findViewById(R.id.toLogin)
        emailErr = findViewById(R.id.emailErr)
        firstNameErr = findViewById(R.id.firstNameErr)
        lastNameErr = findViewById(R.id.lastNameErr)
        bDateErr = findViewById(R.id.bDateErr)
        genderErr = findViewById(R.id.genderErr)
        cityErr = findViewById(R.id.cityErr)
        regionErr = findViewById(R.id.regionErr)
        passwordErr = findViewById(R.id.passwordErr)
        checkBox = findViewById(R.id.checkbox)

        email = findViewById(R.id.userEmail)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        bDate = findViewById(R.id.bDate)
        gender = findViewById(R.id.gender)
        city = findViewById(R.id.city)
        region = findViewById(R.id.region)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirmPassword)
        userRegister = findViewById(R.id.userRegister)
        txtAgreeTerms = findViewById(R.id.txtAgreeTerms)
        layoutSignupFields = findViewById(R.id.layoutSignupFields)
        linearLayoutMessage = findViewById(R.id.linearLayoutMessage)
        layoutLoading = findViewById(R.id.layoutLoading)
        signUpLoading = findViewById(R.id.signUpLoading)
        btnGotIt = findViewById(R.id.btnGotIt)

        presenter = RegistrationPresenter(activity())
        (gender!!.getChildAt(0) as RadioButton).isChecked = true
    }

    private fun initListener() {
        userRegister?.setOnClickListener(activity()::onClick)
        bDate?.setOnClickListener(activity()::onClick)
        txtAgreeTerms?.setOnClickListener(activity()::onClick)
        btnGotIt?.setOnClickListener(activity()::onClick)
        checkBox!!.setOnCheckedChangeListener{ _, isChecked ->
            userRegister!!.isEnabled = isChecked }
        checkBox!!.setOnClickListener(activity()::onClick)
        toLogin?.setOnClickListener(activity()::onClick)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.userRegister -> {
                showLoading()
                val selectedId: Int = gender!!.checkedRadioButtonId
                genderButton = findViewById(selectedId)
                val age = Services.convertAge(bDate?.text.toString())
                if(age < 12){
                    context().toast("Age must be 13 years old and up.")
                    hideLoading()
                    return
                }
                presenter?.onRegister(
                    RegistrationModel(
                        email?.text.toString(),
                        firstName?.text.toString(),
                        lastName?.text.toString(),
                        bDate?.text.toString(),
                        genderButton?.text.toString().toLowerCase(Locale.getDefault()),
                        city?.text.toString(),
                        region?.text.toString(),
                        password?.text.toString(),
                        confirmPassword?.text.toString()))
            }
            R.id.bDate -> presenter?.openBDateSelector()
            R.id.txtAgreeTerms -> presenter?.showTermsDialog()
            R.id.btnGotIt -> Services.nextIntent(this, LoginActivity::class.java)
            R.id.checkbox -> {
                presenter?.showTermsDialog()
            }
            R.id.toLogin -> Services.nextIntent(this, LoginActivity::class.java)
        }
    }

    private fun showLoading() {
        userRegister!!.visibility = View.GONE
        layoutLoading!!.visibility = View.VISIBLE
        signUpLoading!!.visibility = View.VISIBLE
        signUpLoading!!.show()
    }

    fun hideLoading(){
        userRegister!!.visibility = View.VISIBLE
        layoutLoading!!.visibility = View.GONE
        signUpLoading!!.visibility = View.GONE
        signUpLoading!!.hide()
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

    override fun getBDate(date: String) {
        bDate?.text = date
    }

    override fun getEmailErrView(): TextView {
        return emailErr!!
    }

    override fun getFirstNameErrView(): TextView {
        return firstNameErr!!
    }

    override fun getLastNameErrView(): TextView {
        return lastNameErr!!
    }

    override fun getTxtAgreements(): TextView {
        return txtAgreeTerms!!
    }

    override fun getBDateErrView(): TextView {
        return bDateErr!!
    }

    override fun getGenderErrView(): TextView {
        return genderErr!!
    }

    override fun getCityErrView(): TextView {
        return cityErr!!
    }

    override fun getRegionErrView(): TextView {
        return regionErr!!
    }

    override fun getPasswordErrView(): TextView {
        return passwordErr!!
    }

    override fun getCheckBox(): CheckBox {
        return checkBox!!
    }

    override fun getLayoutMessage(): LinearLayout {
        return linearLayoutMessage!!
    }

    override fun getLayoutSignUp(): LinearLayout {
        return layoutSignupFields!!
    }

    override fun setLoadingVisibility() {
        layoutLoading!!.visibility = View.GONE
        signUpLoading!!.visibility = View.GONE
        signUpLoading!!.hide()
        userRegister!!.visibility = View.VISIBLE
    }
}
