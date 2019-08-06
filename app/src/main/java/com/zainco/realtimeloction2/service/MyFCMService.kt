package com.zainco.realtimeloction2.service

import android.os.Build
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zainco.realtimeloction2.utils.Common

class MyFCMService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        message?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendNotificationWithChannel(message)
            } else {
                sendNotification()
            }
        }
        Log.i("FCMSERVICE message", message?.data.toString())

    }

    private fun sendNotification() {

    }

    private fun sendNotificationWithChannel(message: RemoteMessage) {
        val data = message.data
        val title = "Friend Requests"
        val content = "New Friends Request From ${data.get(Common.FROM_NAME)}"
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        Log.i("FCMSERVICE token", token!!)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val tokens = FirebaseDatabase.getInstance()
                .getReference(Common.Tokens)
            tokens.child(user.uid).setValue(token)
        }

    }
}
