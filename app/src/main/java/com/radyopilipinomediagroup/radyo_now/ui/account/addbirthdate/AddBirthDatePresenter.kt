package com.radyopilipinomediagroup.radyo_now.ui.account.addbirthdate

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.*
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.radyopilipinomediagroup.radyo_now.local.SessionManager
import com.radyopilipinomediagroup.radyo_now.model.profile.ProfileDetailsModel
import com.radyopilipinomediagroup.radyo_now.model.profile.ProfileDetailsResponse
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.account.login.LoginActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.toast
import java.lang.Exception
import java.util.*

class AddBirthDatePresenter(var view : AddBirthDateActivity):
    AbstractPresenter<AddBirthDateActivity>(view) {

    private var isCheckedAgree = false
    private var detailsModel: ProfileDetailsModel? = null
    private var tokenKey: String? = null
    private var monthResult: String? = ""
    private var dayResult: String? = ""
    private var dateResult: String? = ""

    init {
        tokenKey = view.intent.getStringExtra("dataKey").toString()
        getProfileDetails()
    }

    fun openBDateSelector(){
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val dialog = object: DatePickerDialog(
            view.context(),
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            selectedDate, year, month, day
        ){}

        cal.set((cal.get(Calendar.YEAR) - 15), 0, 1)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    var selectedDate = DatePickerDialog.OnDateSetListener(object :
        DatePickerDialog.OnDateSetListener,
            (DatePicker, Int, Int, Int) -> Unit {
        override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {}
        override fun invoke(p1: DatePicker, p2: Int, p3: Int, p4: Int) {
            monthResult = (if (p3 + 1 < 10) "0" + (p3 + 1).toString() else (p3 + 1).toString())
            dayResult = (if (p4 < 10) "0" + (p4).toString() else (p4).toString())
            val date = "$monthResult/$dayResult/$p2"

            val age = Services.convertAge(date)
            if(age < 12) {
                view.context().toast("Age must be 13 years old and up.")
                return
            }

            view.getBDate(date)
            view.setErrorVisible(8)

            //Date formatted this will be passed on API.
            val dateFormatted = "$p2-$monthResult-$dayResult"
            dateResult = dateFormatted
        }
    })

    fun showTermsDialog() {
        val dialog = Dialog(view.context())
        dialog.setContentView(com.radyopilipinomediagroup.radyo_now.R.layout.dialog_terms_condition)
        val window = dialog.window!!.attributes
        window.width = android.view.WindowManager.LayoutParams.MATCH_PARENT
        window.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val imgCloseTerms = dialog.findViewById<ImageView>(com.radyopilipinomediagroup.radyo_now.R.id.imgCloseTerms)
        val layoutBottom = dialog.findViewById<LinearLayout>(com.radyopilipinomediagroup.radyo_now.R.id.layoutBottom)
        val chckBoxAgree = dialog.findViewById<CheckBox>(com.radyopilipinomediagroup.radyo_now.R.id.chckBoxAgree)

        imgCloseTerms.setOnClickListener {
            dialog.dismiss() }
        layoutBottom.setOnClickListener {
            checkAgreeEnabled()
            dialog.dismiss() }

        view.getCheckBox().isChecked = isCheckedAgree
        chckBoxAgree.isChecked = isCheckedAgree
        chckBoxAgree!!.setOnClickListener{
            checkAgreeEnabled()
            dialog.dismiss()
        }

        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    private fun checkAgreeEnabled(){
        isCheckedAgree = !isCheckedAgree
        view.getCheckBox().isChecked = isCheckedAgree
    }

    private fun getProfileDetails() {
        getRepositories?.getProfileDetails(tokenKey!!, object :
            RetrofitService.ResultHandler<ProfileDetailsResponse> {
            override fun onSuccess(data: ProfileDetailsResponse?) {
                setDetailsModel(data?.data)
            }

            override fun onError(error: ProfileDetailsResponse?) {
                Log.d("Error", error?.message!!)
            }

            override fun onFailed(message: String) {
                Log.d("Failed", message)
            }
        })
    }

    private fun setDetailsModel(data: ProfileDetailsResponse.Data?) {
        val firstName = if (data?.firstName.isNullOrEmpty()) "N/A" else data?.firstName
        val lastName = if (data?.lastName.isNullOrEmpty()) "N/A" else data?.lastName
        detailsModel = ProfileDetailsModel(firstName, lastName,
                "", "", "", "")
    }

    fun updateProfileDetails() {
        val age = Services.convertAge(view.bDate?.text.toString())
        when {
            view.bDate?.text.toString().isEmpty() -> {
                view.setErrorVisible(0)
            }
            age < 12 -> {
                view.context().toast("Age must be 13 years old and up.")
                return
            }
            else -> {
                view.setErrorVisible(8)
                detailsModel?.dateBirth = dateResult
                getRepositories?.updateDateOfBirth(tokenKey!!, detailsModel!!, "sso", object :
                    RetrofitService.ResultHandler<ProfileDetailsResponse> {
                    override fun onSuccess(data: ProfileDetailsResponse?) {
                        try {
                            getSessionManager?.setData(SessionManager.SESSION_TOKEN, tokenKey)

                            //Set the login status of the user
                            getSessionManager?.setData(SessionManager.SESSION_STATUS, "logged_in")
                            Services.nextIntent(view.activity(), DashboardActivity::class.java)
                            view.activity().toast("Successfully Logged In!")
                            view.setLoadingVisibility()
                        } catch (e: Exception) { }
                    }

                    override fun onError(error: ProfileDetailsResponse?) {
                        view.setLoadingVisibility()
                        //Set visibility and text of the error.
                        view.setErrorVisible(0)
                        view.setErrorText(error?.errors?.dateOfBirth?.get(0).toString())
                    }

                    override fun onFailed(message: String) {
                        view.setLoadingVisibility()
                        Toast.makeText(
                            view.context(),
                            "Something wen't wrong, Please Try Again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }

    fun facebookGmailSignOut(){
        try {
            //Facebook Sign Out
            LoginManager.getInstance().logOut()

            //Google Sign Out
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleSignInClient = GoogleSignIn.getClient(view.context(), gso)
            googleSignInClient.signOut()
            Services.nextIntent(view.activity(), LoginActivity::class.java)
        } catch (e : Exception) {}
    }

    interface View : AbstractPresenter.AbstractView {
        fun setErrorText(error: String)
        fun setErrorVisible(visible: Int)
        fun getBDate(date: String)
        fun getCheckBox() : CheckBox
        fun setLoadingVisibility()
    }
}