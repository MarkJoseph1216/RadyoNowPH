package com.radyopilipinomediagroup.radyo_now.model.security

import com.google.gson.annotations.SerializedName

class LoginModel(
    @SerializedName("email")
    var username : String? = "",
    @SerializedName("password")
    var password : String? = ""
) {
}