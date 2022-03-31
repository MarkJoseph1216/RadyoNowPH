package com.radyopilipinomediagroup.radyo_now.model.stations

import com.google.gson.annotations.SerializedName
import com.radyopilipinomediagroup.radyo_now.model.Meta

class TuneInModelResultModel(
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
        @SerializedName("broadcast_wave_url")
        var broadWave : String? = "",
        @SerializedName("description")
        var description : String? = "",
        @SerializedName("logo")
        var logo : String? = "",
        @SerializedName("type")
        var type : String? = "",
        @SerializedName("live_content")
        var liveContent : Any
    ){

    }
}