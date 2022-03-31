package com.radyopilipinomediagroup.radyo_now.model.ads

import com.google.gson.annotations.SerializedName

class AdsModel(
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    var data: List<Data>
){
    class Data(
        @SerializedName("id")
        var id: Int? = 0,
        @SerializedName("title")
        var title: String? = "",
        @SerializedName("code")
        var code: String? = "",
        @SerializedName("type")
        var type: String? = "",
        @SerializedName("duration_from")
        var durationFrom: String? = "",
        @SerializedName("duration_to")
        var durationTo: String? = "",
        @SerializedName("location_type")
        var locationType: String? = "",
        @SerializedName("location")
        var location: String? = "",
        @SerializedName("section")
        var section: String? = "",
        @SerializedName("active")
        var active: Boolean? = false,
        @SerializedName("assets")
        var assets: List<Assets>
    ){
        class Assets(
            @SerializedName("image_url")
            var imageUrl: String? = "",
            @SerializedName("link")
            var link: String? = ""
        )
    }
}