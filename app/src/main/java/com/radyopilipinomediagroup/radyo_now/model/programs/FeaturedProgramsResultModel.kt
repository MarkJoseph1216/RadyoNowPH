package com.radyopilipinomediagroup.radyo_now.model.programs

import com.google.gson.annotations.SerializedName
import com.radyopilipinomediagroup.radyo_now.model.Meta

class FeaturedProgramsResultModel(
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
        @SerializedName("app_user_favorite")
        var isFavorite : Boolean? = false,
        @SerializedName("program")
        var program : ProgramsModel.Data
    ){

    }
}