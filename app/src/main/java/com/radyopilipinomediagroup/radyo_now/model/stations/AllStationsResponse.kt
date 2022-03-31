package com.radyopilipinomediagroup.radyo_now.model.stations

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AllStationsResponse {
    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: List<Data>? = null

    class Data {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("thumbnail")
        @Expose
        var thumbnail: String? = null

        @SerializedName("content_url")
        @Expose
        var contentUrl: String? = null

        @SerializedName("format")
        @Expose
        var format: String? = null

        @SerializedName("type")
        @Expose
        var type: String? = null

        @SerializedName("featured")
        @Expose
        var featured: Boolean? = null
    }
}