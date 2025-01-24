package com.test.my.app.config_sdk

data class SdkConfig(
    var environment : String = "",
    var apiBaseUrl : String = "",
    var staticBaseUrl : String = "",
    var partnerCode : String = "",
    var saltUrl : String = "",
    var ivKey : ByteArray = byteArrayOf()
)
