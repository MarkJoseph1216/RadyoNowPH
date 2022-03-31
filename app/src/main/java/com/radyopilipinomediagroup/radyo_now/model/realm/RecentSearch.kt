package com.radyopilipinomediagroup.radyo_now.model.realm

import androidx.room.PrimaryKey
import io.realm.RealmObject

open class RecentSearch (

    @PrimaryKey
    var id: Int? = null,

    var programSearch: String? = null

) : RealmObject()