package com.zainco.realtimeloction2.listeners

interface IFirebaseLoadDone {
    fun onFirebaseLoadUsernameDone(lstEmail: List<String>)
    fun onFirebaseLoadFalied(message: String)
}