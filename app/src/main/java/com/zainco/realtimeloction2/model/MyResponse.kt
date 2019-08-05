package com.zainco.realtimeloction2.model

class MyResponse {
    var multiCastId: Long = 0
    var success: Int = 0
    var failure: Int = 0
    var canonical_ids: Int = 0
    var results: MutableList<String>? = null
}