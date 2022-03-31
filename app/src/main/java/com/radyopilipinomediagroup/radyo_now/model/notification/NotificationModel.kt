package com.radyopilipinomediagroup.radyo_now.model.notification

import androidx.room.PrimaryKey
import io.realm.RealmObject

open class NotificationModel (
    var id: Int? = 0,
    var format: String? = "",
    var title: String? = "",
    var body: String? = "",
    var contentId: String? = "",
    var date: String? = "",
    var isRead: Boolean? = false
)