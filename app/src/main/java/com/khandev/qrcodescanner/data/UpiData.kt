package com.khandev.qrcodescanner.data

data class UpiData(
    val upiId: String?, // UPI ID
    val payeeName: String?,
    val amount: String?,
    val currency: String?,
    val transactionNote: String?,
    val transactionRefId: String?,
    val merchantCode: String?,
    val url: String?
)

