package com.radyopilipinomediagroup.radyo_now.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProfileDeactivate {
    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("errors")
    var errors : ProfileDetailsResponse.Errors? = null

    class Errors(
        @SerializedName("avatar")
        var avatar : List<String>,
        @SerializedName("date_of_birth")
        var dateOfBirth : List<String>,
        @SerializedName("city")
        var city : List<String>,
        @SerializedName("region")
        var region : List<String>
    )
}