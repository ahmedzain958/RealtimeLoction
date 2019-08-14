package com.zainco.realtimeloction2.utils

import com.zainco.realtimeloction2.model.User
import com.zainco.realtimeloction2.remote.IFCMService
import com.zainco.realtimeloction2.remote.RetrofitClient
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

class Common {
    companion object {
         var trackingUser: User?=null
        val PUBLIC_LOCATION: String = "PublicLocation"
        val FRIEND_REQUEST: String="FriendRequest"
        val FROM_UID: String = "from_uid"
        val FROM_EMAIL: String = "from_email"
        val TO_UID: String = "to_uid"
        val TO_EMAIL: String = "to_email"
        val ACCEPT_LIST: String = "Accept_List"
        const val Tokens: String = "Tokens"
        const val USER_UID_SAVE_KEY: String = "SaveUid"
          var loggedUser: User?=null
        const val USER_INFORMATION: String = "UserInformation"
        fun getFCMService(): IFCMService {
            return RetrofitClient.getClient("https://fcm.googleapis.com/")
                .create(IFCMService::class.java)
        }

        fun convertTimeStampToDate(time: Long): Date {
            return Date(Timestamp(time).time)
        }

        fun getDateFormatted(date: Date): String? {

            return SimpleDateFormat("dd-MM-yyyy HH:mm").format(date).toString()
        }
    }

}
