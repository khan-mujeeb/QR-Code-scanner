package com.khandev.qrcodescanner.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.khandev.qrcodescanner.data.QrCodeDatabase
import com.khandev.qrcodescanner.data.QrCodeEntity
import com.khandev.qrcodescanner.data.repository.DBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QrCodeViewModel(application: Application) : AndroidViewModel(application) {

    val dao = QrCodeDatabase.getDataBase(application).qrCodeDao()
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