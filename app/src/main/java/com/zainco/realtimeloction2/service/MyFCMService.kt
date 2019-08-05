package com.zainco.realtimeloction2.service

import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFCMService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        message?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendNotificationWithChannel()
            } else {
                sendNotification()
            }
        }
        Log.i("FCMSERVICE message", message?.data.toString())

    }

    private fun sendNotification() {

    }

    private fun sendNotificationWithChannel() {

    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        Log.i("FCMSERVICE token", token!!)

    }
}
