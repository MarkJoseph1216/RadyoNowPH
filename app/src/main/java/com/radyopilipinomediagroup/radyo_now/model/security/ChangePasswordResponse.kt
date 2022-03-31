package com.radyopilipinomediagroup.radyo_now.model.security

import com.google.gson.annotations.SerializedName

class ChangePasswordResponse (
    @SerializedName("message")
    var message : String? = "",
    @SerializedName("errors")
    var errors : Errors? = null
) {
    class Errors(
        @SerializedName("password")
        var password : List<String>,
        @SerializedName("new_password")
        var new_password : List<String>,
        @SerializedName("new_password_confirmation")
        var new_password_confirmation : List<String>
    )
}