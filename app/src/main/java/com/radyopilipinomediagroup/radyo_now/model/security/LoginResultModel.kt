package com.radyopilipinomediagroup.radyo_now.model.security

import com.google.gson.annotations.SerializedName

class LoginResultModel (
    @SerializedName("message")
    var message : String? = "",
    @SerializedName("errors")
    var errors : Errors? = null,
    @SerializedName("data")
    var data : LoginDataToken? = null
        ){
    class Errors(
        @SerializedName("email")
        var email : List<String>,
        @SerializedName("password")
        var password : List<String>
    )
}