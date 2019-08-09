package com.zainco.realtimeloction2.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.zainco.realtimeloction2.R

class NotificationHelper constructor(context: Context) : ContextWrapper(context) {
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    companion object {
        const val EDMT_CHANNEL_ID = "com.zainco.realtimeloction2"
        const val EDMT_CHANNEL_NAME = "realtimeloction2"
    }

    private val notificationManager: NotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        // Building message notification channel

        val notificationChannel = NotificationChannel(
            EDMT_CHANNEL_ID,
            EDMT_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.setShowBadge(true)
        notificationChannel.setSound(uri, null)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(notificationChannel)
    }

    fun createNotificationBuilder(
        title: String,
        body: String,
        cancelAble: Boolean = true,
        pendingIntent: PendingIntent? = null,
        channelId: String = EDMT_CHANNEL_ID
    ): Notification.Builder {
        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            Notification.Builder(applicationContext, channelId)
        else
            Notification.Builder(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.ic_launcher)
            builder.setColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
        } else
            builder.setSmallIcon(R.mipmap.ic_launcher)
        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent)
        builder.setContentTitle(title)
            .setContentText(body)
            .setStyle(Notification.BigTextStyle().bigText(body))
            .setAutoCancel(cancelAble)
        return builder
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun deleteChannel(channelId: String) = apply {
        notificationManager.deleteNotificationChannel(channelId)
    }

    fun makeNotification(builder: Notification.Builder, notificationId: Int) = apply {
        notificationManager.notify(notificationId, builder.build())
    }

    fun cancelNotification(notificationId: Int) = apply {
        notificationManager.cancel(notificationId)
    }
}