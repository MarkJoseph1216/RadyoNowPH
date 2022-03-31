package com.radyopilipinomediagroup.radyo_now.model

import com.google.gson.annotations.SerializedName
import com.radyopilipinomediagroup.radyo_now.model.playlist.PlaylistContentsResultModel

class ContentDetailsResponse {
    @SerializedName("message")
    private var message: String? = null

    @SerializedName("data")
    private var data: Data? = null

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getData(): Data? {
        return data
    }

    fun setData(data: Data?) {
        this.data = data
    }

    class Data {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("description")
        var description: String? = null

        @SerializedName("thumbnail")
        var thumbnail: Any? = null

        @SerializedName("content_url")
        var contentUrl: String? = null

        @SerializedName("format")
        var format: String? = null

        @SerializedName("type")
        var type: String? = null

        @SerializedName("age_restriction")
        var ageRestriction: String? = null

        @SerializedName("broadcast_date")
        var broadcastDate: String? = null

        @SerializedName("active")
        var active: Boolean? = null

        @SerializedName("featured")
        var featured: Boolean? = null

        @SerializedName("created_at")
        var createdAt: String? = null

        @SerializedName("updated_at")
        var updatedAt: String? = null

        @SerializedName("app_user_favorite")
        var isFavorite: Boolean? = false

        @SerializedName("program")
        var program: PlaylistContentsResultModel.Data.Program? = null
    }
}