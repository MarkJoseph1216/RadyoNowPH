package com.radyopilipinomediagroup.radyo_now.model.programs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProgramsModel(
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    var data: List<Data>) {
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

        @SerializedName("contents_count")
        @Expose
        var contentsCount: Int? = null

        @SerializedName("thumbnail")
        @Expose
        var thumbnail: String? = null
    }
}