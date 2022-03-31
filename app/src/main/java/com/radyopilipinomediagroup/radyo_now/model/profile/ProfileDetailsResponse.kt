package com.radyopilipinomediagroup.radyo_now.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ProfileDetailsResponse {
    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null

    @SerializedName("errors")
    var errors : Errors? = null

    class Data {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("first_name")
        @Expose
        var firstName: String? = null

        @SerializedName("last_name")
        @Expose
        var lastName: String? = null

        @SerializedName("date_of_birth")
        @Expose
        var dateOfBirth: String? = null

        @SerializedName("gender")
        @Expose
        var gender: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("avatar")
        @Expose
        var avatar: Any? = null

        @SerializedName("city")
        @Expose
        var city: String? = null

        @SerializedName("region")
        @Expose
        var region: String? = null
    }

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