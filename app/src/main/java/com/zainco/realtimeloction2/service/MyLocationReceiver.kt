package com.zainco.realtimeloction2.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.LocationResult
import com.google.firebase.database.FirebaseDatabase
import com.zainco.realtimeloction2.utils.Common
import io.paperdb.Paper

class MyLocationReceiver : BroadcastReceiver() {
    val publicLocation = FirebaseDatabase.getInstance().getReference(Common.PUBLIC_LOCATION)

    companion object {
        const val ACTION = "com.zainco.realtimeloction2.UPDATE_LOCATION"
    }

    lateinit var uid: String
    override fun onReceive(context: Context, intent: Intent?) {
        Paper.init(context)
        uid = Paper.book().read(Common.USER_UID_SAVE_KEY)
        if (intent != null) {
            val action = intent.action
            if (action == ACTION) {
                val result = LocationResult.extractResult(intent)
                if (result != null) {
                    val location = result.lastLocation
                    if (Common.loggedUser != null) {//app in foregroung
                        publicLocation.child(Common.loggedUser!!.uid)
                            .setValue(location)
                    } else {//app killed
                        publicLocation.child(uid)
                            .setValue(location)
                    }
                }
            }
        }
    }

}
