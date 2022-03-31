package com.radyopilipinomediagroup.radyo_now.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.radyopilipinomediagroup.radyo_now.R
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardActivity
import com.radyopilipinomediagroup.radyo_now.ui.dashboard.DashboardPresenter
import org.greenrobot.eventbus.EventBus

class NotificationService: Service() {

    private var notificationManager: NotificationManager? = null
    private var notificationChannel: NotificationChannel? = null
    private val channelId = "com.radyopilipinomediagroup.radyo_now"
    private val description = "RadyoNowPH"
    lateinit var notification: Notification
    var mediaPlayer: AudioPlayer? = AudioPlayer(this)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val smallView = RemoteViews(packageName, R.layout.import_notification_layout)
        val bigView = RemoteViews(packageName, R.layout.import_notification_expanded_layout)

        when (intent!!.action) {
            NOTIFY_PREVIOUS -> EventBus.getDefault().post("Previous")
            NOTIFY_NEXT -> EventBus.getDefault().post("Next")
            NOTIFY_PLAY -> {
                setVisibility(bigView, smallView, 8, 0)
                EventBus.getDefault().post("Play")
            }
            NOTIFY_PAUSE -> {
                setVisibility(bigView, smallView, 0, 8)
                EventBus.getDefault().post("Pause")
            }
            NOTIFY_DELETE -> {
                EventBus.getDefault().post("Close")
                destroyNotification(this)
            }
        }
        return START_STICKY
    }

    private fun setVisibility(
        bigView: RemoteViews,
        smallView: RemoteViews,
        playVisible: Int,
        pauseVisible: Int
    ){
        bigView.setViewVisibility(R.id.status_bar_play, playVisible)
        bigView.setViewVisibility(R.id.status_bar_pause, pauseVisible)

        smallView.setViewVisibility(R.id.status_bar_play, playVisible)
        smallView.setViewVisibility(R.id.status_bar_pause, pauseVisible)

        // Build the content of the notification
        val nBuilder = getNotificationBuilder(
            this,
            "Music Player",
            "Control Audio",
            R.drawable.ic_radyo_icon,
            "RadyoNow"
        )

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

    fun getNotificationBuilder(
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
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel?.enableLights(true)
                notificationChannel?.lightColor = Color.GREEN
                notificationChannel?.enableVibration(false)
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
            .setContentIntent(pendingIntent)
            .setTicker(notificationTicker)
        builder.setOngoing(true)

        return builder
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val resultIntent = Intent(context, DashboardActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        return PendingIntent.getActivity(
            context, 0,
            resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    //Removing the notification builder
    fun destroyNotification(context: Context) = try {
        //Terminal the audio player.
        destroyAudioPlayer()
        //Stop the service.
        val serviceIntent = Intent(context, NotificationService::class.java)
        context.stopService(serviceIntent)
        //Terminate the notification.
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID_BIG_CONTENT)
    } catch (e: Exception) { destroyAudioPlayer() }

    private fun destroyAudioPlayer(){
        mediaPlayer?.stopMediaPlayer()
        mediaPlayer?.isReady = false
    }
}