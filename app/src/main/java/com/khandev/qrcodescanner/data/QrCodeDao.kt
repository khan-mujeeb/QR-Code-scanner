package com.khandev.qrcodescanner.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QrCodeDao {


    @Query("SELECT * FROM qrcode_table")
    fun getAllData(): LiveData<List<QrCodeEntity>>

    @Insert
    suspend fun insert(qrCodeData: QrCodeEntity)

    @Delete
    suspend fun deleteEntry(qrData: QrCodeEntity)

    @Query("DELETE FROM qrcode_table")
    fun deleteAllEntries()
}