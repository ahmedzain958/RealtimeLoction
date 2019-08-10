package com.zainco.realtimeloction2.utils

import com.zainco.realtimeloction2.model.User
import com.zainco.realtimeloction2.remote.IFCMService
import com.zainco.realtimeloction2.remote.RetrofitClient

class Common {
    companion object {
        val FRIEND_REQUEST: String="FriendRequest"
        val FROM_UID: String = "from_uid"
        val FROM_EMAIL: String = "from_email"
        val TO_UID: String = "to_uid"
        val TO_EMAIL: String = "to_email"
        val ACCEPT_LIST: String = "Accept_List"
        const val Tokens: String = "Tokens"
        const val USER_UID_SAVE_KEY: String = "SaveUid"
      lateinit  var loggedUser: User
        const val USER_INFORMATION: String = "UserInformation"
        fun getFCMService(): IFCMService {
            return RetrofitClient.getClient("https://fcm.googleapis.com/")
                .create(IFCMService::class.java)
        }
    }

}
