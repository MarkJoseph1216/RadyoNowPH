package com.radyopilipinomediagroup.radyo_now.model.realm

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.radyopilipinomediagroup.radyo_now.utils.Services
import io.realm.RealmObject
import java.text.SimpleDateFormat
import java.util.*

open class NotificationLocal(
    @PrimaryKey
    var id: Int? = 0,
    var format: String? = "",
    var title: String? = "",
    var body: String? = "",
    var contentId: String? = "",
    var date: String? = "",
    var isRead: Boolean? = false
) : RealmObject() {
    fun getDate() : Date {
        return SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.getDefault()
        ).parse(date!!)!!
    }
}