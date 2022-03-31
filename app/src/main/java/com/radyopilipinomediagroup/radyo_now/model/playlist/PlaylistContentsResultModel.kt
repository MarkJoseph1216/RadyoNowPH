package com.radyopilipinomediagroup.radyo_now.model.playlist

import com.google.gson.annotations.SerializedName
import com.radyopilipinomediagroup.radyo_now.model.Meta

class PlaylistContentsResultModel(
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
        @SerializedName("description")
        var description : String? = "",
        @SerializedName("thumbnail")
        var thumbnail : String? = "",
        @SerializedName("content_url")
        var contentUrl : String? = "",
        @SerializedName("format")
        var format : String? = "",
        @SerializedName("type")
        var type : String? = "",
        @SerializedName("age_restriction")
        var ageRestriction : String? = "",
        @SerializedName("broadcast_date")
        var broadcastDate : String? = "",
        @SerializedName("active")
        var active : Boolean? = false,
        @SerializedName("featured")
        var featured : Boolean? = false,
        @SerializedName("created_at")
        var createdAt : String? = "",
        @SerializedName("updated_at")
        var updatedAt : String? = "",
        @SerializedName("program")
        var program : Program
    ){
        class Station(
            @SerializedName("id")
            var id : Int? = 0,
            @SerializedName("name")
            var name : String? = "",
            @SerializedName("description")
            var description : String? = "",
            @SerializedName("logo")
            var logo : String? = "",
            @SerializedName("type")
            var type : String? = ""
        )

        class Program(
            @SerializedName("id")
            var id : Int? = 0,
            @SerializedName("name")
            var name : String? = "",
            @SerializedName("description")
            var description : String? = "",
            @SerializedName("contents_count")
            var contentCount : Int? = 0,
            @SerializedName("thumbnail")
            var thumbnail : String? = ""
        )
    }
}