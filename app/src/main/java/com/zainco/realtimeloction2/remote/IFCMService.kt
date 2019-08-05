package com.zainco.realtimeloction2.remote

import com.zainco.realtimeloction2.model.MyResponse
import com.zainco.realtimeloction2.model.Request
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IFCMService {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAA5v9lkx0:APA91bHg_skFgHNvC6-WX_8OHnm_FtqWMBfcReYfpMK5MM4IkCDMuVsMzbCT3Ug68zQEHx1nQIjveJnt6fubgYe7kb9hd0F4qu_kyeKghyvio-S09LadMzinve8uMoH7LV4tYoXIBzaz"
    )
    @POST("fcm/send")
    fun sendFriendrequestToUser(@Body body: Request): Observable<MyResponse>
}