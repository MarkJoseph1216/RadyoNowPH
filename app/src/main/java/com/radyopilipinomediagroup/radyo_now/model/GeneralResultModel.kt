package com.radyopilipinomediagroup.radyo_now.model

import com.google.gson.annotations.SerializedName

class GeneralResultModel(
    @SerializedName("message")
    var message : String? = "",
    @SerializedName("errors")
    var errors : Any? = null
){
}