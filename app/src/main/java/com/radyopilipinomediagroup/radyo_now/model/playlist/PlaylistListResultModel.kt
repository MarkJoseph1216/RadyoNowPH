package com.radyopilipinomediagroup.radyo_now.model.playlist

import com.google.gson.annotations.SerializedName
import com.radyopilipinomediagroup.radyo_now.model.Meta

class PlaylistListResultModel (
    @SerializedName("message")
    var message : String? = "",
    @SerializedName("data")
    var data : List<Data>,
    @SerializedName("meta")
    var meta : Meta
        ){
    class Data(
        @SerializedName("id")
        var id : Int? = 0,
        @SerializedName("name")
        var name : String? = "",
        @SerializedName("contents_count")
        var contentsCount : Int? = 0,
        @SerializedName("thumbnails")
        var thumbnail : MutableList<String>? = mutableListOf()
    )
}