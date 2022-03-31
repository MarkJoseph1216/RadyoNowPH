package com.radyopilipinomediagroup.radyo_now.model.security

import com.google.gson.annotations.SerializedName

class SSOResultModel(
    @SerializedName("message")
    var message : String ? = "",
    @SerializedName("data")
    var data: LoginDataToken? = null
){


}