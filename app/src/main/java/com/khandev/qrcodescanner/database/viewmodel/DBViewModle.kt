package com.khandev.qrcodescanner.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.khandev.qrcodescanner.database.AppDatabase
import com.khandev.qrcodescanner.database.data.QrCodeEntity
import com.khandev.qrcodescanner.database.repository.DBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DBViewModle(application: Application) : AndroidViewModel(application) {

    val dao = AppDatabase.getDataBase(application).qrCodeDao()
    private val repository = DBRepository(dao)

    val scannedQr: LiveData<List<QrCodeEntity>> = repository.scannedQrCode

    fun insert(qrData: QrCodeEntity) {
        viewModelScope.launch {
            repository.insert(qrData)
        }
    }

    /*
    delete Entery
    */
    fun deleteEntery(qrData: QrCodeEntity) {
        viewModelScope.launch {
            repository.deleteEntry(qrData)
        }
    }

    /*
    delete All entries
     */
    fun deleteAllEntries() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllEntries()
        }
    }


}