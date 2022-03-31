package com.radyopilipinomediagroup.radyo_now.model.stations

import com.google.gson.annotations.SerializedName
import com.radyopilipinomediagroup.radyo_now.model.Meta

class StationListResultModel(
    @SerializedName("message")
    var message : String? = "",
    @SerializedName("data")
    var stations : List<Station>,
    @SerializedName("meta")
    var meta : Meta
) {
    class Station(
        @SerializedName("id")
        var id : Int? = 0,
        @SerializedName("broadcast_wave_url")
        var broadwave : String? = "",
        @SerializedName("description")
        var description : String? = "",
        @SerializedName("name")
        var name : String? = "",
        @SerializedName("logo")
        var logo : String? = "",
        @SerializedName("type")
        var type : String? = ""
    )
}