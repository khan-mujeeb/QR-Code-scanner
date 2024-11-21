package com.khandev.qrcodescanner.utlis

object Categories {
    const val ALL = "all"
    const val URL = "url"
    const val TEXT = "text"
    const val EMAIL = "email"
    const val SMS = "sms"
    const val WIFI = "wifi"
    const val CONTACT = "contact"
    const val PAYMENT = "payment"

    // Convert the constants to a list
    val categoriesList = listOf(
        ALL,
        URL,
        TEXT,
        CONTACT,
        WIFI,
        PAYMENT

    )
}