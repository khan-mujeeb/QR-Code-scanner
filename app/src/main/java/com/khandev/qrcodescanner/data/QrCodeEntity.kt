package com.khandev.qrcodescanner.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "qrcode_table")
data class QrCodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "qr_data") val qrcodeData: String,
    @ColumnInfo(name = "category") val category: String,
)
