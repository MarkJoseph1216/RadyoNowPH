package com.radyopilipinomediagroup.radyo_now.model.playlist

import com.google.gson.annotations.SerializedName

class PlaylistDetailsResultModel(
    @SerializedName("message")
    var message : String? = "",
    @SerializedName("data")
    var data : Data
) {
    class Data(
        @SerializedName("id")
        var id : Int? = 0,
        @SerializedName("name")
        var name : String? = "",
        @SerializedName("contents_count")
        var contentsCount : Int? = 0,
        @SerializedName("active")
        var active : Boolean? = false,
        @SerializedName("created_at")
        var createdAt : String? = "",
        @SerializedName("updated_at")
        var updatedAt : String? = ""
    )
}