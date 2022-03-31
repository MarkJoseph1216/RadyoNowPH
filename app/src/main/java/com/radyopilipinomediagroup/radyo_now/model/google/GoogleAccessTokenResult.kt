package com.radyopilipinomediagroup.radyo_now.model.google

import com.google.gson.annotations.SerializedName

class GoogleAccessTokenResult(
    @SerializedName("access_token")
    var accessToken : String? = "",
    @SerializedName("expires_in")
    var expiresIn : Int? = 0,
    @SerializedName("scope")
    var scope : String? = "",
    @SerializedName("token_type")
    var tokenType : String? = "",
    @SerializedName("id_token")
    var idToken : String? = ""
) {
}