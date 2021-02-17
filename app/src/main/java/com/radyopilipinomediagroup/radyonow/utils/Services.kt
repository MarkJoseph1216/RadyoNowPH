package com.radyopilipinomediagroup.radyonow.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import java.util.*

class Services {
    companion object {
        fun nextIntent(context: Activity, toClass : Class<*>){
            val intent = Intent(context, toClass)
            context.startActivity(intent)
            context.finish()
        }
    }
}