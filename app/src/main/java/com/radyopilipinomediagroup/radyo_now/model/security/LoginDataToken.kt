package com.radyopilipinomediagroup.radyo_now.model.security

import com.google.gson.annotations.SerializedName

class LoginDataToken (@SerializedName("id")
                      var id: String? = "",
                      @SerializedName("name")
                      var name: String? = "",
                      @SerializedName("first_name")
                      var first_name: String? = "",
                      @SerializedName("last_name")
                      var last_name: String? = "",
                      @SerializedName("date_of_birth")
                      var dateOfBirth: String? = "",
                      @SerializedName("email")
                      var email: String? = "",
                      @SerializedName("avatar")
                      var avatar: String? = "",
                      @SerializedName("token")
                      var token: String? = "",
                      @SerializedName("password_expired")
                      var password_expired: String? = "") {

}