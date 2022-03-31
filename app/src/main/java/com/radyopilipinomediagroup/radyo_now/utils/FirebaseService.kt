package com.radyopilipinomediagroup.radyo_now.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.local.SessionManager
import com.radyopilipinomediagroup.radyo_now.model.notification.PayloadNotificationModel
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import org.json.JSONObject
import kotlin.random.Random

private const val CHANNEL_ID = "Radyo_Now"

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.d("GetToken: ", newToken)
    }

    override fun handleIntent(intent: Intent) {
//        Log.d("FCM", "handleIntent ")
//        Log.d("BUNDLE_FCM", Gson().toJson(intent.extras))

        val bundle = intent.extras ?: return
        val json = JSONObject()
        val keys = bundle.keySet()
        keys.forEach {
            try {
                json.put(it, JSONObject.wrap(bundle.get(it)))
            } catch (e: Exception){
                println("handleIntent: ${e.localizedMessage}")
            }
        }
        Log.d("BUNDLE_FCM", json.toString())

        removeFirebaseOriginalNotifications()
//        val bundle = intent.extras ?: return
        val jsonObject = Gson().fromJson(json.toString(), PayloadNotificationModel.Bundle.MMap::class.java) ?: return
        Log.d("OBJECT_NOTIF_HI", Gson().toJson(jsonObject))
        val session = SessionManager(applicationContext)
        val notifSettings = session.getSessionSettings()
        println("handeNotification: ${!notifSettings!!}")
        if(!notifSettings!!) return
//        super.handleIntent(intent)
        handeNotification(jsonObject)
    }

//    override fun onMessageReceived(message: RemoteMessage) {
//        val jsonObject = Gson().fromJson(Gson().toJson(message), PayloadNotificationModel::class.java) ?: return
//        Log.d("OBJECT_NOTIF_OMR: ", Gson().toJson(jsonObject))
//        val notifBundle = jsonObject.bundle ?: return
//        handeNotification(notifBundle)
//    }

    fun handeNotification(jsonObject:  PayloadNotificationModel.Bundle.MMap){
        if(jsonObject.gcmNotificationTitle?.isEmpty()!!
            ||jsonObject.gcmNotificationBody?.isEmpty()!!) return

        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
//        session.setNotification(jsonObject)
        val newIntent = Intent("notification_broadcast",null,this, DashboardActivity::class.java)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt() //realm.model?.id!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(
            notificationManager
        )

        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //FLAG_ACTIVITY_CLEAR_TOP //FLAG_ACTIVITY_SINGLE_TOP
            .putExtra("notification", Gson().toJson(jsonObject)) //jsonObject  realm.model
        localBroadcastManager.sendBroadcast(newIntent)
        val pendingIntent = PendingIntent.getActivity(this, 0, newIntent, FLAG_ONE_SHOT) //
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(jsonObject.gcmNotificationTitle)
            .setContentText(jsonObject.gcmNotificationBody)
            .setSmallIcon(R.drawable.ic_radyo_icon)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(notificationID, notification)
    }

    private fun removeFirebaseOriginalNotifications() {

        //check notificationManager is available
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        //check api level for getActiveNotifications()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //if your Build version is less than android 6.0
            //we can remove all notifications instead.
            //notificationManager.cancelAll();
            return
        }

        //check there are notifications
        val activeNotifications = notificationManager.activeNotifications ?: return

        //remove all notification created by library(super.handleIntent(intent))
        for (tmp in activeNotifications) {
            Log.d(
                "FCM Notification",
                "tag/id: " + tmp.tag + " / " + tmp.id
            )
            val tag = tmp.tag
            val id = tmp.id

            //trace the library source code, follow the rule to remove it.
            if (tag != null && tag.contains("FCM-Notification")) notificationManager.cancel(tag, id)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "RadyoNow"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "RadyoNow Application"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }
}