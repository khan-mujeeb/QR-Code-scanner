package com.khandev.qrcodescanner.utlis

object Categories {
    const val ALL = "all"
    const val URL = "url"
    const val TEXT = "text"
    const val VCARD = "vcard"
    const val GEO = "geo"
    const val EMAIL = "email"
    const val PHONE = "phone"
    const val SMS = "sms"
    const val WIFI = "wifi"
    const val CALENDAR = "calendar"
    const val ISBN = "isbn"
    const val PRODUCT = "product"
    const val UNKNOWN = "unknown"
    const val CONTACT = "contact"

    // Convert the constants to a list
    val categoriesList = listOf(
        Categories.ALL,
        Categories.URL,
        Categories.TEXT,
        Categories.CONTACT,
        Categories.GEO,
        Categories.EMAIL,
        Categories.PHONE,
        Categories.SMS,
        Categories.WIFI

    )
}