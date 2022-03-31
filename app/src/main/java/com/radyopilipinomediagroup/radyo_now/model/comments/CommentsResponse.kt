package com.radyopilipinomediagroup.radyo_now.model.comments

import com.google.gson.annotations.SerializedName
import com.radyopilipinomediagroup.radyo_now.model.Meta


class CommentsResponse(
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    var data: List<Data>,
    @SerializedName("meta")
    var meta: Meta
) {

    class Data {
        @SerializedName("id")
        var id: String? = null
        @SerializedName("comment")
        var comment: String? = null
        @SerializedName("created_at")
        var createdAt: String? = null
        @SerializedName("appUser")
        var appUser: AppUser? = null
        class AppUser {
            @SerializedName("id")
            var id: Int? = null
            @SerializedName("name")
            var name: String? = null
            @SerializedName("avatar")
            var avatar: String? = null
        }
    }
}