package com.radyopilipinomediagroup.radyo_now.model

import com.google.gson.annotations.SerializedName

class Meta(
    @SerializedName("pagination")
    var pagination : Pagination
){
    class Pagination(
        @SerializedName("total")
        var total : Int? = 0,
        @SerializedName("count")
        var count : Int? = 0,
        @SerializedName("per_page")
        var perPage : Int? = 0,
        @SerializedName("current_page")
        var currentPage : Int? = 0,
        @SerializedName("total_pages")
        var totalPages : Int? = 0
    )
}