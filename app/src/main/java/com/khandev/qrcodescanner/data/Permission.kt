package com.khandev.qrcodescanner.data

data class Permission(
    val permission: String,
    val requestCode: Int,
    val onGranted: () -> Unit
)
