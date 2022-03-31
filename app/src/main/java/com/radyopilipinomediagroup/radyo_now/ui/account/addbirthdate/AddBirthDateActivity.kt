package com.radyopilipinomediagroup.radyo_now.ui.account.addbirthdate

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.wang.avi.AVLoadingIndicatorView

class AddBirthDateActivity : AppCompatActivity(), AbstractPresenter.ContextView<AddBirthDateActivity>,
    AddBirthDatePresenter.View, View.OnClickListener {

    var bDate : TextView? = null
    var bDateErr : TextView? = null
    private var presenter : AddBirthDatePresenter? = null
    private var txtAgreeTerms : TextView? = null
    private var checkBox : CheckBox? = null
    private var layoutLoading : RelativeLayout? = null
    private var signUpLoading : AVLoadingIndicatorView? = null
    private var userRegister: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_birthdate)
        initComponents()
        initListeners()
    }

    private fun initComponents() {
        bDate = findViewById(R.id.bDate)
        txtAgreeTerms = findViewById(R.id.txtAgreeTerms)
        bDateErr = findViewById(R.id.bDateErr)
        checkBox = findViewById(R.id.checkbox)
        userRegister = findViewById(R.id.userRegister)
        layoutLoading = findViewById(R.id.layoutLoading)
        signUpLoading = findViewById(R.id.signUpLoading)
        presenter = AddBirthDatePresenter(activity())
    }

    private fun initListeners(){
        bDate?.setOnClickListener(this::onClick)
        txtAgreeTerms?.setOnClickListener(this::onClick)
        checkBox!!.setOnCheckedChangeListener{ _, isChecked ->
            userRegister!!.isEnabled = isChecked }
        checkBox!!.setOnClickListener(this::onClick)
        userRegister?.setOnClickListener(this::onClick)
    }

    override fun activity(): AddBirthDateActivity {
        return this
    }

    override fun context(): Context {
        return this
    }

    override fun applicationContext(): Context {
        return this.applicationContext
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.userRegister -> {
                showLoading()
                presenter?.updateProfileDetails()
            }
            R.id.bDate -> presenter?.openBDateSelector()
            R.id.txtAgreeTerms -> presenter?.showTermsDialog()
        }
    }

    private fun showLoading() {
        userRegister!!.visibility = View.GONE
        layoutLoading!!.visibility = View.VISIBLE
        signUpLoading!!.visibility = View.VISIBLE
        signUpLoading!!.show()
    }

    override fun setErrorText(error: String) {
        bDateErr?.text = error
    }

    override fun setErrorVisible(visible: Int) {
        bDateErr?.visibility = visible
    }

    override fun getBDate(date: String) {
        bDate?.text = date
    }

    override fun getCheckBox(): CheckBox {
        return checkBox!!
    }

    override fun setLoadingVisibility() {
        layoutLoading!!.visibility = View.GONE
        signUpLoading!!.visibility = View.GONE
        signUpLoading!!.hide()
        userRegister!!.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        presenter?.facebookGmailSignOut()
        finish()
    }
}