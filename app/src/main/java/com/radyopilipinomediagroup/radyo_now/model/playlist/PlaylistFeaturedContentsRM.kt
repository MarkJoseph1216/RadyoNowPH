package com.radyopilipinomediagroup.radyo_now.model.playlist

import com.google.gson.annotations.SerializedName
import com.radyopilipinomediagroup.radyo_now.model.Meta

class PlaylistFeaturedContentsRM(
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
        @SerializedName("thumbnail")
        var thumbnail : String? = "",
        @SerializedName("content_url")
        var contentUrl : String? = "",
        @SerializedName("format")
        var format : String? = "",
        @SerializedName("type")
        var type : String?
    )
}