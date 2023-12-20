package com.khandev.qrcodescanner.data.repository

import androidx.lifecycle.LiveData
import com.khandev.qrcodescanner.data.QrCodeDao
import com.khandev.qrcodescanner.data.QrCodeEntity

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

    /*
    deleteEntry
    */
    suspend fun deleteEntry(data: QrCodeEntity) = dao.deleteEntry(data)

    /*
    Delete All Entry
     */
    fun deleteAllEntries() {
        dao.deleteAllEntries()
    }

}