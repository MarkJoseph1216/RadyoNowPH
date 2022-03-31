package com.radyopilipinomediagroup.radyo_now.model.notification

import com.google.gson.annotations.SerializedName
import com.radyopilipinomediagroup.radyo_now.model.Meta

class NotificationResponseModel(
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    var data: List<Data>? = mutableListOf<Data>(),
    @SerializedName("meta")
    var meta: Meta
) {
    class Data(
        @SerializedName("id")
        var id: Int? = 0,
        @SerializedName("name")
        var name: String? = "",
        @SerializedName("description")
        var description: String? = "",
        @SerializedName("trigger_datetime")
        var triggerDatetime: String? = "",
        @SerializedName("active")
        var active: Boolean? = false,
        @SerializedName("content")
        var content: Content? = null
    ){
        class Content(
            @SerializedName("id")
            var id: Int? = 0,
            @SerializedName("name")
            var name: String? = "",
            @SerializedName("description")
            var description: String? = "",
            @SerializedName("thumbnail")
            var thumbnail: String? = "",
            @SerializedName("content_url")
            var contentUrl: String? = "",
            @SerializedName("format")
            var format: String? = "",
            @SerializedName("type")
            var type: String? = "",
            @SerializedName("featured")
            var featured: Boolean? = false,
            @SerializedName("broadcast_date")
            var broadcastDate: String? = "",
            @SerializedName("landing_page")
            var landingPage: String? = "",
            @SerializedName("app_user_favorite")
            var isFavorite: Boolean? = false
        )
    }
}