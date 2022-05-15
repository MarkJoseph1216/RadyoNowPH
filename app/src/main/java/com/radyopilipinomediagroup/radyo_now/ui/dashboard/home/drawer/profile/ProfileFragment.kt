package com.radyopilipinomediagroup.radyo_now.ui.dashboard.home.drawer.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.facebook.FacebookSdk.getApplicationContext
import com.google.gson.Gson
import com.mikhaellopez.circularimageview.CircularImageView
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.model.profile.ProfileDetailsModel
import com.radyopilipinomediagroup.radyo_now.model.profile.ProfileDetailsResponse
import com.radyopilipinomediagroup.radyo_now.ui.AbstractPresenter
import com.radyopilipinomediagroup.radyo_now.ui.account.changepassword.ChangePasswordActivity
import com.radyopilipinomediagroup.radyo_now.utils.Services
import com.radyopilipinomediagroup.radyo_now.utils.capitalizeWords
import com.radyopilipinomediagroup.radyo_now.utils.toast
import java.io.IOException

class ProfileFragment : Fragment(), AbstractPresenter.ContextView<FragmentActivity>
    ,ProfilePresenter.View, View.OnClickListener {

    private var profileView : View? = null
    private var profilePresenter : ProfilePresenter? = null
    private var btnChangePassword : Button? = null
    private var btnCancel : Button? = null
    private var txtEditProfile : TextView? = null
    private var layoutProfile : LinearLayout? = null
    private var layoutEditProfile : NestedScrollView? = null
    private var txtUploadError : TextView? = null
    private var txtName : TextView? = null
    private var txtEmail : TextView? = null
    private var txtGender : TextView? = null
    private var txtUserBirth : TextView? = null
    private var txtUserCity : TextView? = null
    private var txtUserRegion : TextView? = null
    private var btnDeactivate: TextView? = null
    private var profilePic : CircularImageView? = null
    private var changePic : ImageView? = null

    private var userFirstName : EditText? = null
    private var userLastName : EditText? = null
    private var userGender : RadioGroup? = null
    var userBirth : EditText? = null
    private var userCity : EditText? = null
    private var userRegion : EditText? = null
    private var btnUploadPhoto: Button? = null
    private var btnSave : Button? = null
    private var bitmap: Bitmap? = null
    var filePath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        profileView = inflater.inflate(R.layout.fragment_profile, container, false)
        initComponents()
        initDeclaration()
        initOnClickListeners()
        return profileView
    }

    private fun initComponents() {
        txtUploadError = profileView?.findViewById(R.id.txtUploadError)
        profilePic = profileView?.findViewById(R.id.profilePic)
        layoutProfile = profileView?.findViewById(R.id.layoutProfile)
        layoutEditProfile = profileView?.findViewById(R.id.layoutEditProfile)
        btnChangePassword = profileView?.findViewById(R.id.btnChangePassword)
        txtEditProfile = profileView?.findViewById(R.id.txtEditProfile)
        changePic = profileView?.findViewById(R.id.changePic)
        btnCancel = profileView?.findViewById(R.id.btnCancel)
        txtName = profileView?.findViewById(R.id.txtName)
        txtEmail = profileView?.findViewById(R.id.txtEmail)
        txtGender = profileView?.findViewById(R.id.txtGender)

        txtUserBirth = profileView?.findViewById(R.id.txtUserBirth)
        txtUserCity = profileView?.findViewById(R.id.txtUserCity)
        txtUserRegion = profileView?.findViewById(R.id.txtUserRegion)
        btnUploadPhoto = profileView?.findViewById(R.id.btnUploadPhoto)
        btnSave = profileView?.findViewById(R.id.btnSave)

        userFirstName = profileView?.findViewById(R.id.userFirstName)
        userLastName = profileView?.findViewById(R.id.userLastName)
        userBirth = profileView?.findViewById(R.id.userBirth)
        userBirth?.isFocusableInTouchMode = false
        userGender = profileView?.findViewById(R.id.userGender)
        userCity = profileView?.findViewById(R.id.userCity)
        userRegion = profileView?.findViewById(R.id.userRegion)
        btnDeactivate = profileView?.findViewById(R.id.btnDeactivate)
    }

    private fun initDeclaration(){
        profilePresenter = ProfilePresenter(this)
        profilePresenter?.setToolbarSetup()
        profilePresenter?.getProfileDetails()
    }

    private fun initOnClickListeners(){
        btnChangePassword!!.setOnClickListener(this::onClick)
        txtEditProfile!!.setOnClickListener(this::onClick)
        btnCancel!!.setOnClickListener(this::onClick)
        btnSave!!.setOnClickListener(this::onClick)
        btnUploadPhoto!!.setOnClickListener(this::onClick)
        changePic!!.setOnClickListener(this::onClick)
        userBirth?.setOnClickListener(this::onClick)
        btnDeactivate?.setOnClickListener(this::onClick)
    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.btnChangePassword -> {
                val intent = Intent(context(), ChangePasswordActivity::class.java)
                intent.putExtra("email", txtEmail?.text.toString())
                context!!.startActivity(intent)
//                ChangePasswordActivity().passData(txtEmail?.text.toString())
            }
            R.id.txtEditProfile -> viewEditProfile()
            R.id.btnUploadPhoto -> profilePresenter?.uploadProfilePhoto()
            R.id.btnCancel -> {
                profilePresenter?.getProfileDetails()
                hideEditProfile()
            }
            R.id.btnSave -> {
                println("R.id.btnSave")
                try {
                    val genId: Int? = userGender?.checkedRadioButtonId
                    val selectedValue = profileView?.findViewById(genId!!) as RadioButton
                    val formattedDate = Services.convertDate(userBirth?.text.toString(),
                        "MM/dd/yyyy", "yyyy-MM-dd")
                    val age = Services.convertAge(userBirth?.text.toString())
                    if(age < 12) {
                        context().toast("Age must be 13 years old and up.")
                        return
                    }

                    profilePresenter?.updateProfileDetails(
                        ProfileDetailsModel(
                            userFirstName?.text.toString(),
                            userLastName?.text?.toString(),
                            formattedDate,
                            selectedValue.text.toString().toLowerCase(),
                            userCity?.text?.toString(),
                            userRegion?.text?.toString()
                        )
                    )
                } catch (e: Exception) {
                    println("R.id.btnSave: ${e.localizedMessage}")
                }
            }
            R.id.changePic -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (profilePresenter?.isPermission() == true) {
                        profilePresenter?.openGallery()
                    } else {
                        profilePresenter?.requestPermission() // Code for permission
                    }
                }
            }
            R.id.userBirth -> profilePresenter?.openBDateSelector()
            R.id.btnDeactivate -> profilePresenter?.showDeactivateDialog()
        }
    }

    private fun viewEditProfile(){
        txtEditProfile!!.visibility = View.INVISIBLE
        layoutEditProfile!!.visibility = View.VISIBLE
        changePic!!.visibility = View.VISIBLE
        layoutProfile!!.visibility = View.GONE
    }

    private fun hideEditProfile(){
        txtUploadError?.visibility = View.GONE
        btnUploadPhoto?.visibility = View.GONE
        changePic!!.visibility = View.GONE
        txtEditProfile!!.visibility = View.VISIBLE
        layoutEditProfile!!.visibility = View.GONE
        layoutProfile!!.visibility = View.VISIBLE
    }

    override fun activity(): FragmentActivity {
       return activity!!
    }

    override fun context(): Context {
        return context!!
    }

    override fun applicationContext(): Context {
        return activity().applicationContext
    }

    @SuppressLint("SimpleDateFormat")
    override fun setProfileDetails(details: ProfileDetailsResponse?) {
        try {
            println("setProfileDetails: ${Gson().toJson(details)}")
            Glide.with(context())
                .load(details?.data?.avatar)
                .placeholder(R.drawable.ic_radyo_icon)
                .into(profilePic!!)
            txtName!!.text = details!!.data!!.name
            txtEmail!!.text = details.data!!.email
            txtUserCity!!.text = if (details.data!!.city != "null"
                && details.data!!.city != null) details.data!!.city
            else "-"
            txtUserRegion!!.text = if (details.data!!.region != "null"
                && details.data!!.region != null) details.data!!.region
            else "-"
            txtGender!!.text = details.data!!.gender?.capitalizeWords()

            try {
                txtUserBirth!!.text = Services.convertDate(
                    details.data?.dateOfBirth,
                    "yyyy-MM-dd", "MM/dd/yyyy"
                )
            } catch (e: Exception) {}

            userFirstName!!.setText(details.data!!.firstName)
            userLastName!!.setText(details.data!!.lastName)
            userCity!!.setText(details.data!!.city)
            userRegion!!.setText(details.data!!.region)

            try {
                userBirth!!.setText(
                    Services.convertDate(
                        details.data?.dateOfBirth,
                        "yyyy-MM-dd", "MM/dd/yyyy"
                    )
                )
            } catch (e: Exception) {}

            if (details.data!!.gender != "null" && details.data!!.gender != null) {
                if (details.data!!.gender?.toLowerCase() == "male")
                        (userGender!!.getChildAt(0) as RadioButton).isChecked = true
               else (userGender!!.getChildAt(1) as RadioButton).isChecked = true
            } else txtGender!!.text = "-"
        } catch (e: Exception) {
            e.message
        }
    }

    override fun setBirthDate(date: String) {
        userBirth?.setText(date)
    }

    override fun setTextErrorResult(errorResult: String) {
        txtUploadError?.text = errorResult
    }

    override fun setErrorVisible(visible: Int) {
        txtUploadError?.visibility = visible
    }

    override fun setEditLayoutVisible() {
        txtUploadError?.visibility = View.GONE
        btnUploadPhoto?.visibility = View.GONE
        changePic!!.visibility = View.GONE
        txtEditProfile!!.visibility = View.VISIBLE
        layoutEditProfile!!.visibility = View.GONE
        layoutProfile!!.visibility = View.VISIBLE
    }

    override fun setButtonUploadVisible(visible: Int) {
        btnUploadPhoto?.visibility = visible
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == profilePresenter?.SELECT_FILE)
            if (data != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().contentResolver,
                        data.data
                    )
                    filePath = profilePresenter?.getPath(context(), data.data!!)
                    btnUploadPhoto?.visibility = View.VISIBLE
                    txtUploadError?.visibility = View.GONE
                } catch (e: IOException) {
                    Toast.makeText(context, "Something wen't wrong, Please Try Again.", Toast.LENGTH_SHORT).show()
                    btnUploadPhoto?.visibility = View.GONE
                }
            }
            profilePic?.setImageBitmap(bitmap)
        }
    }
}