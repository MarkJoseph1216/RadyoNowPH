package com.radyopilipinomediagroup.radyo_now.model.security

import com.google.gson.annotations.SerializedName

class ForgotPassResultModel(
    @SerializedName("message")
    var message : String? = "",
    @SerializedName("errors")
    var errors : Errors? = null
) {
    class Errors(
        @SerializedName("email")
        var email : List<String>
    )
}