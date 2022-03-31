package com.radyopilipinomediagroup.radyo_now.model.stations

import com.google.gson.annotations.SerializedName

class StationDetailsResultModel (
    @SerializedName("message")
    var message : String? = "",
    @SerializedName("data")
    var data : Data? = null
        ){
    class Data(
        @SerializedName("id")
        var id: Int? = 0,
        @SerializedName("name")
        var name: String? = "",
        @SerializedName("broadcast_wave_url")
        var broadwaveUrl: String? = "",
        @SerializedName("logo")
        var logo: String? = "",
        @SerializedName("description")
        var description: String? = "",
        @SerializedName("type")
        var type: String? = "",
        @SerializedName("live_content")
        var liveContent: Any,
        @SerializedName("active")
        var active: Boolean,
        @SerializedName("featured")
        var featured: Boolean,
        @SerializedName("created_at")
        var createdAt: String? = "",
        @SerializedName("updated")
        var updated: String? = ""
    ) {
//        class Live(
//            @SerializedName("id")
//            var id: Int? = 0,
//            @SerializedName("name")
//            var name: String? = "",
//            @SerializedName("description")
//            var description: String? = "",
//            @SerializedName("thumbnail")
//            var thumbnail: String? = "",
//            @SerializedName("content_url")
//            var contentUrl: String? = "",
//            @SerializedName("format")
//            var format: String? = "",
//            @SerializedName("type")
//            var type: String? = "",
//            @SerializedName("featured")
//            var featured: Boolean,
//            @SerializedName("broadcast_date")
//            var broadcastDate: String? = "",
//            @SerializedName("landing_page")
//            var landingPage: String? = ""
//        )
    }
}