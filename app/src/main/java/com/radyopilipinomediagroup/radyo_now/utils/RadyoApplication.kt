package com.radyopilipinomediagroup.radyo_now.utils

import android.app.Application
import io.branch.referral.Branch
import io.realm.Realm
import io.realm.RealmConfiguration

class RadyoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //Realm initialization
        Realm.init(this)
        val configuration = RealmConfiguration.Builder()
            .name("RadyoNow.db")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(0)
            .build()
        Realm.setDefaultConfiguration(configuration)

        // Branch object initialization
        Branch.getAutoInstance(this)
    }
}