package com.radyopilipinomediagroup.radyo_now.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity

const val NOTIFY_PREVIOUS = "com.radyonow.previous"
const val NOTIFY_DELETE = "com.radyonow.delete"
const val NOTIFY_PAUSE = "com.radyonow.pause"
const val NOTIFY_PLAY = "com.radyonow.play"
const val NOTIFY_NEXT = "com.radyonow.next"
const val STARTFOREGROUND_ACTION = "com.radyonow.startforeground"
const val NOTIFICATION_ID_BIG_CONTENT = 99

class NotificationBuilder(var notificationIntentClass: Class<*> = DashboardActivity::class.java) {

    private var notificationManager: NotificationManager? = null
    private var notificationChannel: NotificationChannel? = null
    private val channelId = "com.radyopilipinomediagroup.radyo_now"
    private val description = "RadyoNowPH"

    var smallView : RemoteViews? = null
    var bigView : RemoteViews? = null

    fun showMusicPlayerNotification(
        context: Context,
        isPaused: Boolean, bimap: Bitmap,
        stationName: String,
        stationDate: String,
        stationRestriction: String) {

        // Notification through notification manager
        lateinit var notification: Notification
        // Using RemoteViews to bind custom layouts into Notification
        smallView = RemoteViews(context.packageName, R.layout.import_notification_layout)
        bigView = RemoteViews(context.packageName, R.layout.import_notification_expanded_layout)

        // Showing default album image
        smallView?.setViewVisibility(R.id.status_bar_icon, View.VISIBLE)
        smallView?.setViewVisibility(R.id.status_bar_album_art, View.GONE)

        smallView?.setImageViewBitmap(R.id.status_bar_icon, bimap)
        bigView?.setImageViewBitmap(R.id.status_bar_album_art, bimap)

        if (isPaused) {
            setVisibility(0, 8)
        } else {
            setVisibility(8, 0)
        }

        setListeners(context, stationName, stationDate, stationRestriction)

        // Build the content of the notification
        val nBuilder = getNotificationBuilder(
            context,
            "Music Player",
            "Control Audio",
            R.drawable.ic_radyo_icon,
            "RadyoNow")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nBuilder.setCustomBigContentView(bigView)
            nBuilder.setCustomContentView(smallView)
            notification = nBuilder.build()
        } else {
            notification = nBuilder.build()
            notification.contentView = smallView
            notification.bigContentView = bigView
        }

        // Notification through notification manager
        notification.flags = Notification.FLAG_ONGOING_EVENT
        notificationManager?.notify(NOTIFICATION_ID_BIG_CONTENT, notification)
    }

    private fun setVisibility(playVisible: Int, pauseVisible: Int){
        bigView?.setViewVisibility(R.id.status_bar_play, playVisible)
        bigView?.setViewVisibility(R.id.status_bar_pause, pauseVisible)

        smallView?.setViewVisibility(R.id.status_bar_play, playVisible)
        smallView?.setViewVisibility(R.id.status_bar_pause, pauseVisible)
    }

    private fun setListeners(
        context: Context,
        stationName: String,
        stationDate: String,
        stationRestriction: String) {

        setPendingIntent(context, NOTIFY_DELETE, R.id.status_bar_collapse)
        setPendingIntent(context, NOTIFY_PREVIOUS, R.id.status_bar_prev)
        setPendingIntent(context, NOTIFY_NEXT, R.id.status_bar_next)
        setPendingIntent(context, NOTIFY_PLAY, R.id.status_bar_play)
        setPendingIntent(context, NOTIFY_PAUSE, R.id.status_bar_pause)

        bigView?.setTextViewText(R.id.status_bar_track_name, stationName)
        smallView?.setTextViewText(R.id.status_bar_track_name, stationName)

        if (Services.checkValidDate(stationDate)) {
            setStationDateDetails(Services.convertDate(stationDate))
        } else {
            setStationDateDetails(stationDate)
        }

        bigView?.setTextViewText(
            R.id.status_bar_restriction, stationRestriction)
    }

    private fun setStationDateDetails(stationDate: String){
        bigView?.setTextViewText(
            R.id.status_bar_artist_date,
            Html.fromHtml(stationDate)
        )
        smallView?.setTextViewText(
            R.id.status_bar_artist_date,
            Html.fromHtml(stationDate)
        )
    }

    private fun setPendingIntent(context: Context, actionName: String, componentId: Int){
        val intentPlay = Intent(context, NotificationService::class.java)
        intentPlay.action = actionName
        val pendingIntent = PendingIntent.getService(
            context,
            0,
            intentPlay,
            PendingIntent.FLAG_UPDATE_CURRENT)

        bigView?.setOnClickPendingIntent(componentId, pendingIntent)
        smallView?.setOnClickPendingIntent(componentId, pendingIntent)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getNotificationBuilder(
        context: Context,
        notificationTitle: String,
        notificationText: String,
        notificationIconId: Int,
        notificationTicker: String
    ): Notification.Builder {
        // Define the notification channel for newest Android versions
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent = getPendingIntent(context)
        lateinit var builder: Notification.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (null == notificationChannel) {
                notificationChannel = NotificationChannel(
                    channelId,
                    description,
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationChannel?.enableLights(false)
                notificationChannel?.lightColor = Color.GREEN
                notificationChannel?.enableVibration(false)
                notificationChannel?.setSound(null,null)
                notificationManager?.createNotificationChannel(notificationChannel!!)
            }
            builder = Notification.Builder(context, channelId)
        } else {
            builder = Notification.Builder(context)
        }

        // Build the content of the notification
        builder.setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(notificationIconId)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, notificationIconId))
            .setAutoCancel(true)
            .setDefaults(0)
            .setContentIntent(pendingIntent)
            .setTicker(notificationTicker)
        builder.setOngoing(true)
        builder.setVisibility(Notification.VISIBILITY_PUBLIC)

        return builder
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val resultIntent = Intent(context, notificationIntentClass)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        return PendingIntent.getActivity(
            context, 0,
            resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}