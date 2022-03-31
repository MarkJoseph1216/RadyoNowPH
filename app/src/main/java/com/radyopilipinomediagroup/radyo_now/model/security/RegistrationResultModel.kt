package com.radyopilipinomediagroup.radyo_now.model.security

import com.google.gson.annotations.SerializedName

class RegistrationResultModel(
    @SerializedName("message")
    var message : String? = "",
    @SerializedName("errors")
    var errors : Errors? = null
) {
    class Errors(
        @SerializedName("first_name")
        var firstName : List<String>,
        @SerializedName("last_name")
        var lastName : List<String>,
        @SerializedName("email")
        var email : List<String>,
        @SerializedName("password")
        var password : List<String>,
        @SerializedName("date_of_birth")
        var dateOfBirth : List<String>,
        @SerializedName("gender")
        var gender : List<String>,
        @SerializedName("city")
        var city : List<String>,
        @SerializedName("region")
        var region : List<String>
    )
}