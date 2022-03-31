package com.radyopilipinomediagroup.radyo_now.model.notification

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

class PayloadNotificationModel(
    @SerializedName("bundle")
    var bundle: Bundle? = null
) {
    class Bundle(
        @SerializedName("mMap")
        var mMap: MMap? = null
    ) {
        class MMap(
            @SerializedName("google.c.a.m_l")
            var googleCAML: String? = "",
            @SerializedName("google.delivered_priority")
            var googleDeliveredPriority: String? = "",
            @SerializedName("google.sent_time")
            var googleSentTime: Long? = 0,
            @SerializedName("google.ttl")
            var googleTtl: Int? = 0,
            @SerializedName("google.original_priority")
            var googleOriginalPriority: String? = "",
            @SerializedName("gcm.notification.e")
            var gcmNotificationE: String? = "",
            @SerializedName("format")
            var format: String? = "",
            @SerializedName("gcm.notification.color")
            var gcmNotificationColor: String? = "",
            @SerializedName("gcm.notification.image")
            var gcmNotificationImage: String? = "",
            @SerializedName("gcm.notification.title")
            var gcmNotificationTitle: String? = "",
            @SerializedName("from")
            var from: String? = "",
            @SerializedName("content_id")
            var contentId: String? = "",
            @SerializedName("google.message_id")
            var googleMessageId: String? = "",
            @SerializedName("gcm.notification.body")
            var gcmNotificationBody: String? = "",
            @SerializedName("google.c.a.e")
            var googleCAE: String? = "",
            @SerializedName("google.c.sender.id")
            var googleCSenderId: String? = "",
            @SerializedName("collapse_key")
            var collapseKey: String? = ""
        ) {
        }
    }
}