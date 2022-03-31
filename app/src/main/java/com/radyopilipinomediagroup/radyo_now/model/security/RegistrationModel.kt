package com.radyopilipinomediagroup.radyo_now.model.security

import com.google.gson.annotations.SerializedName

class RegistrationModel(
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("first_name")
    var firstName: String? = "",
    @SerializedName("last_name")
    var lastName: String? = "",
    @SerializedName("date_of_birth")
    var bDate: String? = "",
    @SerializedName("gender")
    var gender: String? = "",
    @SerializedName("city")
    var city: String? = "",
    @SerializedName("region")
    var region: String? = "",
    @SerializedName("password")
    var password: String? = "",
    @SerializedName("password_confirmation")
    var repeatPassword: String? = ""
) {

}