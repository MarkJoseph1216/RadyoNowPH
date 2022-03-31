package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.model.profile.ProfileDetailsModel
import com.radyopilipinomediagroup.radyo_now.model.profile.ProfileDetailsResponse
import com.radyopilipinomediagroup.radyo_now.repositories.RetrofitService
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.HomeFragment
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.toast
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*

class ProfilePresenter(var view: ProfileFragment): AbstractPresenter<ProfileFragment>(view) {

    private var PERMISSION_REQUEST_CODE = 1
    var SELECT_FILE = 1
    private var dialog: DatePickerDialog? = null
    private var fragmentManager = view.activity().supportFragmentManager

    private var monthResult: String? = ""
    private var dayViewResult: String? = ""

    var selectedDate = DatePickerDialog.OnDateSetListener(object :
        DatePickerDialog.OnDateSetListener,
            (DatePicker, Int, Int, Int) -> Unit {
        override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {}
        override fun invoke(p1: DatePicker, p2: Int, p3: Int, p4: Int) {
            monthResult = (if (p3 + 1 < 10) "0" + (p3 + 1).toString() else (p3 + 1).toString())
            dayViewResult = (if (p4 < 10) "0" + (p4).toString() else (p4).toString())

            val date = "$monthResult/$dayViewResult/$p2"
            val age = Services.convertAge(date)
            if(age < 12) {
                view.context().toast("Age must be 13 years old and up.")
                return
            }
            view.setBirthDate(date)
        }
    })

    @SuppressLint("SetTextI18n")
    fun setToolbarSetup(){
        val toHomePage = android.view.View.OnClickListener {
            Services.changeFragment(fragmentManager, HomeFragment(), "HomeFragment")
        }
        getToolbarText?.text = "Profile"
        getToolbarDrawerIcon!!.visibility = android.view.View.VISIBLE
        getToolbarLogo!!.visibility = android.view.View.VISIBLE
        getToolbarBack!!.visibility = android.view.View.GONE

        getToolbarBack?.setOnClickListener(toHomePage)
        getToolbarLogo?.setOnClickListener(toHomePage)
    }

    fun isPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            view.context(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {
        requestPermissions(
            view.activity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    fun getProfileDetails() {
        getRepositories?.getProfileDetails(getSessionManager?.getToken()!!, object :
            RetrofitService.ResultHandler<ProfileDetailsResponse> {
            override fun onSuccess(data: ProfileDetailsResponse?) {
                view.setProfileDetails(data)
            }

            override fun onError(error: ProfileDetailsResponse?) {
                Log.d("Error", error?.message!!)
            }

            override fun onFailed(message: String) {
                Log.d("Failed", message)
            }
        })
    }

    fun updateProfileDetails(data: ProfileDetailsModel) {
        getRepositories?.updateProfileDetails(
            getSessionManager?.getToken()!!,
            data,
            "profile",
            object :
                RetrofitService.ResultHandler<ProfileDetailsResponse> {
                override fun onSuccess(data: ProfileDetailsResponse?) {
                    view.setProfileDetails(data)
                    view.setEditLayoutVisible()
                    Toast.makeText(view.context(), "Updated Successfully!", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onError(error: ProfileDetailsResponse?) {
                    Toast.makeText(
                        view.context(),
                        "Please fill up the required fields.",
                        Toast.LENGTH_SHORT
                    ).show()
                    println("ProfileDetailsResponse: ${error?.errors?.city}")
                    println("ProfileDetailsResponse: ${error?.errors?.region}")
                }

                override fun onFailed(message: String) {
                    Toast.makeText(
                        view.context(),
                        "Something wen't wrong, Please Try Again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun uploadProfilePhoto() {
        Toast.makeText(view.context(), "Uploading Photo, Please wait...", Toast.LENGTH_SHORT).show()
        // Map is used to multipart the file using okhttp3.RequestBody
        val file = File(view.filePath!!)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        // MultipartBody.Part is used to send also the actual file name
        val body = MultipartBody.Part.createFormData("avatar", file.name, requestFile)

        getRepositories?.uploadProfilePhoto(getSessionManager?.getToken()!!, body, object :
            RetrofitService.ResultHandler<ProfileDetailsResponse> {
            override fun onSuccess(data: ProfileDetailsResponse?) {
                Toast.makeText(view.context(), "Profile Photo Uploaded!", Toast.LENGTH_SHORT).show()
                view.setProfileDetails(data)
                view.setEditLayoutVisible()
                view.setButtonUploadVisible(8)
            }

            override fun onError(error: ProfileDetailsResponse?) {
                view.setTextErrorResult(error?.errors?.avatar?.get(0).toString())
                view.setErrorVisible(0)
            }

            override fun onFailed(message: String) {
                view.setErrorVisible(8)
                Toast.makeText(
                    view.context(),
                    "Something wen't wrong, Please Try Again.",
                    Toast.LENGTH_SHORT
                ).show()
                println("GetUploadError: ${Gson().toJson(message)}")
            }
        })
    }

    //Getting the filePath from image intent
    fun getPath(context: Context, uri: Uri?): String? {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri!!, proj, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(proj[0])
                result = cursor.getString(column_index)
            }
            cursor.close()
        }
        if (result == null) {
            result = "No file found."
        }
        return result
    }

    fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK //
        view.startActivityForResult(intent, SELECT_FILE)
    }

    fun openBDateSelector(){
        val c = Calendar.getInstance()
        c.add(Calendar.YEAR, -12)
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        dialog = if (view.userBirth?.text.toString() != "") {
            val dates = view.userBirth?.text.toString().split("/").toTypedArray()
            object: DatePickerDialog(
                view.context(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                selectedDate, dates[2].toInt(), dates[0].toInt() - 1, dates[1].toInt()
            ){}
        } else {
            object: DatePickerDialog(
                view.context(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                selectedDate, year, month, day
            ){}
        }
        cal.set((cal.get(Calendar.YEAR) - day), month, 1)
        dialog!!.datePicker.maxDate = Date().time
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()
    }

    interface View : AbstractPresenter.AbstractView {
        fun setProfileDetails(details: ProfileDetailsResponse?)
        fun setBirthDate(date: String)
        fun setTextErrorResult(errorResult: String)
        fun setErrorVisible(visible: Int)
        fun setEditLayoutVisible()
        fun setButtonUploadVisible(visible: Int)
    }
}