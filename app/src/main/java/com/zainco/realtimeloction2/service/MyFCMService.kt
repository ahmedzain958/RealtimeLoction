package com.zainco.realtimeloction2.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zainco.realtimeloction2.R
import com.zainco.realtimeloction2.TestPendingIntentActivity
import com.zainco.realtimeloction2.model.User
import com.zainco.realtimeloction2.utils.Common
import com.zainco.realtimeloction2.utils.NotificationHelper
import com.zainco.realtimeloction2.utils.NotificationHelper.Companion.EDMT_CHANNEL_ID
import kotlin.random.Random

class MyFCMService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        message?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendNotificationWithChannel(message)
            } else {
                sendNotification(message)
            }

            addRequestToUserInformation(message.data)
        }
    }

    private fun addRequestToUserInformation(data: Map<String, String>) {
        val friendRequest = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
            .child(data[Common.TO_UID]!!)
            .child(Common.FRIEND_REQUEST)

        val user = User( data[Common.FROM_EMAIL],data[Common.FROM_UID])
        friendRequest.child(user.uid)
            .setValue(user)
    }

    private fun sendNotification(message: RemoteMessage) {
        val data: MutableMap<String, String> = message.data
        val title = "Friend Requests"
        val content = "New Friend Request from " + data[Common.FROM_EMAIL]
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, EDMT_CHANNEL_ID)
            .setSmallIcon(R.drawable.abc_ratingbar_small_material)
            .setContentTitle(title)
            .setContentText(content)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(false)
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random.nextInt(), builder.build())
    }

    private fun sendNotificationWithChannel(message: RemoteMessage) {
        val data: MutableMap<String, String> = message.data
        val title = "Friend Requests"
        val content = "New Friend Request from " + data[Common.FROM_EMAIL]

        val intent = Intent(this, TestPendingIntentActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notifyPendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val helper = NotificationHelper(this)
        val builder = helper.createNotificationBuilder(
            title, content, false,
            notifyPendingIntent,
            EDMT_CHANNEL_ID
        )
        helper.makeNotification(builder, Random.nextInt())
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        Log.i("FCMSERVICE token", token!!)

    }
}
