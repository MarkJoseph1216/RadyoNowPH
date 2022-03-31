package com.radyopilipinomediagroup.radyo_now.model.stations

import com.google.gson.annotations.SerializedName
import com.radyopilipinomediagroup.radyo_now.model.Meta

class FeaturedStationsResultModel(
    @SerializedName("message")
    var message : String? = "",
    @SerializedName("data")
    var data : List<Data>,
    @SerializedName("meta")
    var meta : Meta
) {
    class Data(
        @SerializedName("id")
        var id : Int? = 0,
        @SerializedName("name")
        var name : String? = "",
        @SerializedName("logo")
        var logo : String? = "",
        @SerializedName("type")
        var type : String? = "",
        @SerializedName("featured_content")
        var featured : Featured? = null
    ){
        class Featured(
            @SerializedName("id")
            var id : Int? = 0,
            @SerializedName("name")
            var name : String? = "",
            @SerializedName("thumbnail")
            var thumbnail : String? = "",
            @SerializedName("content_url")
            var contentUrl : String? = "",
            @SerializedName("format")
            var format : String? = "",
            @SerializedName("type")
            var type : String? = "",
            @SerializedName("featured")
            var featured : String? = "",
            @SerializedName("broadcast_date")
            var broadcast_date : String? = "",
            @SerializedName("landing_page")
            var landing_page : String? = ""
        )
    }
}