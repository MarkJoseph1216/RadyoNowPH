package com.radyopilipinomediagroup.radyo_now.model.deeplink

import com.google.gson.annotations.SerializedName

class DeepLinkResponse(
    @SerializedName("nameValuePairs")
    var data: NameValuePairs? = null
) {
    class NameValuePairs {
        @SerializedName("\$marketing_title")
        var title: String? = null

        @SerializedName("\$deeplink_path")
        var deeplinkPath: String? = null

        @SerializedName("~creation_source")
        var creationSource: Int? = null

        @SerializedName("\$content_type")
        var contentType: String? = null

        @SerializedName("+click_timestamp")
        var clickTimestamp: Int? = null

        @SerializedName("\$uri_redirect_mode")
        private var uriRedirectMode: String? = null

        @SerializedName("+match_guaranteed")
        var matchGuaranteed: Boolean? = null

        @SerializedName("~marketing")
        var marketing: Boolean? = null

        @SerializedName("+clicked_branch_link")
        var clickedBranchLink: Boolean? = null

        @SerializedName("~id")
        var id: Long? = null

        @SerializedName("\$one_time_use")
        var oneTimeUser: Boolean? = null

        @SerializedName("+is_first_session")
        var isFirstSession: Boolean? = null

        @SerializedName("~referring_link")
        var referringLink: String? = null

        @SerializedName("\$ios_deepview")
        var iosDeepView: String? = null
    }
}