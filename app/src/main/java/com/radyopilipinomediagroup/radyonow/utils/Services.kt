package com.radyopilipinomediagroup.radyonow.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.util.*

class Services {
    companion object {
        fun nextIntent(context: Activity, toClass : Class<*>){
            val intent = Intent(context, toClass)
            context.startActivity(intent)
            context.finish()
        }

        fun notAvailable(context: Context){
            Toast.makeText(context, "Feature not Available", Toast.LENGTH_SHORT).show()
        }
    }
}