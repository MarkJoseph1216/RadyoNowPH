package com.radyopilipinomediagroup.radyo_now.ui.account.registration

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.*
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.model.security.RegistrationModel
import com.radyopilipinomediagroup.radyo_now.model.security.RegistrationResultModel
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.toast
import java.util.*

class RegistrationPresenter(var view: RegistrationActivity) : AbstractPresenter<RegistrationActivity>(view) {

    var isCheckedAgree = false

    var selectedDate = DatePickerDialog.OnDateSetListener(object :
        DatePickerDialog.OnDateSetListener,
            (DatePicker, Int, Int, Int) -> Unit {
        override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {}
        override fun invoke(p1: DatePicker, p2: Int, p3: Int, p4: Int) {
            val date: String = (p3 + 1).toString() + "/" + p4.toString() + "/" + p2.toString()
            val age = Services.convertAge(date)
            if(age < 12){
                view.context().toast("Age must be 13 years old and up.")
                return
            }
            view.getBDate(date)
        }
    })

    fun onRegister(userRegDetails: RegistrationModel) {
        validateErrors("", "", "", "", "", "", "", "", "")
        getRepositories?.register(
            userRegDetails,
            object : RetrofitService.ResultHandler<RegistrationResultModel> {
                override fun onSuccess(data: RegistrationResultModel?) {
                    Log.d("ACCOUNT_REGISTRATION", Gson().toJson(data))
                    setLayoutVisibility(8, 0)
                    view.setLoadingVisibility()
                }

                override fun onError(error: RegistrationResultModel?) {
                    view.setLoadingVisibility()
                    validateErrors(
                        error?.message,
                        error?.errors?.email?.get(0),
                        error?.errors?.firstName?.get(0),
                        error?.errors?.lastName?.get(0),
                        error?.errors?.dateOfBirth?.get(0),
                        error?.errors?.gender?.get(0),
                        error?.errors?.city?.get(0),
                        error?.errors?.region?.get(0),
                        error?.errors?.password?.get(0)
                    )
                }

                override fun onFailed(message: String) {
                    view.setLoadingVisibility()
                    Toast.makeText(view.activity(), message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun setLayoutVisibility(layoutFieldsVisible: Int, layoutMessage: Int){
        view.getLayoutSignUp().visibility = layoutFieldsVisible
        view.getLayoutMessage().visibility = layoutMessage
    }

    fun openBDateSelector(){
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val dialog = object:DatePickerDialog(
            view.context(),
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            selectedDate, year, month, day
        ){}

        cal.set((cal.get(Calendar.YEAR) - 15), 0, 1)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun validateErrors(
        message: String?,
        emailErr: String?,
        firstNameErr: String?,
        lastNameErr: String?,
        bDateErr: String?,
        genderErr: String?,
        cityErr: String?,
        regionErr: String?,
        passwordErr: String?
    ){

        if (emailErr != null) {
            if(emailErr.isNotEmpty()){
                view.getEmailErrView().text = emailErr
                view.getEmailErrView().visibility = android.view.View.VISIBLE
            }
        }else view.getEmailErrView().visibility = android.view.View.GONE

        if (firstNameErr != null) {
            if(firstNameErr.isNotEmpty()){
                view.getFirstNameErrView().text = firstNameErr
                view.getFirstNameErrView().visibility = android.view.View.VISIBLE
            }
        }else view.getFirstNameErrView().visibility = android.view.View.GONE

        if (lastNameErr != null) {
            if(lastNameErr.isNotEmpty()){
                view.getLastNameErrView().text = lastNameErr
                view.getLastNameErrView().visibility = android.view.View.VISIBLE
            }
        }else view.getLastNameErrView().visibility = android.view.View.GONE

        if (bDateErr != null) {
            if(bDateErr.isNotEmpty()){
                view.getBDateErrView().text = bDateErr
                view.getBDateErrView().visibility = android.view.View.VISIBLE
            }
        }else view.getBDateErrView().visibility = android.view.View.GONE

        if (genderErr != null) {
            if(genderErr.isNotEmpty()){
                view.getGenderErrView().text = genderErr
                view.getGenderErrView().visibility = android.view.View.VISIBLE
            }
        }else view.getGenderErrView().visibility = android.view.View.GONE

        if (cityErr != null) {
            if(cityErr.isNotEmpty()){
                view.getCityErrView().text = cityErr
                view.getCityErrView().visibility = android.view.View.VISIBLE
            }
        }else view.getCityErrView().visibility = android.view.View.GONE

        if (regionErr != null) {
            if(regionErr.isNotEmpty()){
                view.getRegionErrView().text = regionErr
                view.getRegionErrView().visibility = android.view.View.VISIBLE
            }
        }else view.getRegionErrView().visibility = android.view.View.GONE

        if (passwordErr != null) {
            if(passwordErr.isNotEmpty()){
                view.getPasswordErrView().text = passwordErr
                view.getPasswordErrView().visibility = android.view.View.VISIBLE
            }
        } else view.getPasswordErrView().visibility = android.view.View.GONE

        //Display toast message if the validateErrors is all empty.
        if (message != null)
            if (message != "success" && message != "" && !message.contains("data was invalid"))
            Toast.makeText(view.activity(), message, Toast.LENGTH_SHORT).show()
    }

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

    interface View : AbstractView{
        fun getBDate(date: String)
        fun getEmailErrView() : TextView
        fun getFirstNameErrView() : TextView
        fun getLastNameErrView() : TextView
        fun getBDateErrView() : TextView
        fun getGenderErrView() : TextView
        fun getCityErrView() : TextView
        fun getRegionErrView() : TextView
        fun getPasswordErrView() : TextView
        fun getTxtAgreements() : TextView
        fun getCheckBox() : CheckBox
        fun getLayoutMessage() : LinearLayout
        fun getLayoutSignUp() : LinearLayout
        fun setLoadingVisibility()
    }
}