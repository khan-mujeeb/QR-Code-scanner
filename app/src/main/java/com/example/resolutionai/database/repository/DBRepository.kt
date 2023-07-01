package com.example.resolutionai.database.repository

import androidx.lifecycle.LiveData
import com.example.resolutionai.database.dao.QrCodeDao
import com.example.resolutionai.database.data.QrCodeEntity

class DBRepository(private val dao: QrCodeDao) {

    /*
    get all data
    */
    val scannedQrCode: LiveData<List<QrCodeEntity>> = dao.getAllData()

    /*
      insert new data
     */
    suspend fun insert(qrData: QrCodeEntity) {
        dao.insert(qrData)
    }


}