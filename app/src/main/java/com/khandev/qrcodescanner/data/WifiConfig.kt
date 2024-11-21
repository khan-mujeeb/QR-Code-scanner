package com.khandev.qrcodescanner.data

data class WifiConfig(
    val ssid: String,
    val password: String?,
    val encryption: String?,
    val hidden: Boolean
)
