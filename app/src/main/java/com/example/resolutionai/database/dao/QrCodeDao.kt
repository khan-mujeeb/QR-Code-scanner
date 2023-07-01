package com.example.resolutionai.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.resolutionai.database.data.QrCodeEntity

@Dao
interface QrCodeDao {


    @Query("SELECT * FROM qrcode_table")
    fun getAllData(): LiveData<List<QrCodeEntity>>

    @Insert
    suspend fun insert(qrCodeData: QrCodeEntity)
}