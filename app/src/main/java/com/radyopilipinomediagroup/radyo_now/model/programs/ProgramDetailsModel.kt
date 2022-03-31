package com.radyopilipinomediagroup.radyo_now.model.programs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProgramDetailsModel() {
    @SerializedName("message")
    val message: String? = null
    @SerializedName("data")
    val data: Data? = null

    class Data {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("description")
        @Expose
        var description: String? = null

        @SerializedName("thumbnail")
        @Expose
        var thumbnail: String? = null

        @SerializedName("active")
        @Expose
        var active: Boolean? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null

        @SerializedName("updated_at")
        @Expose
        var updatedAt: String? = null
    }
}