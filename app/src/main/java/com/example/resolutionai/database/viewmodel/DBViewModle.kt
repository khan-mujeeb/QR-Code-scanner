package com.example.resolutionai.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.resolutionai.database.AppDatabase
import com.example.resolutionai.database.data.QrCodeEntity
import com.example.resolutionai.database.repository.DBRepository
import kotlinx.coroutines.launch

class DBViewModle(application: Application): AndroidViewModel(application) {

    val dao = AppDatabase.getDataBase(application).qrCodeDao()
    private val repository = DBRepository(dao)

    val scannedQr: LiveData<List<QrCodeEntity>> = repository.scannedQrCode

    fun insert(qrData: QrCodeEntity) {
        viewModelScope.launch {
            repository.insert(qrData)
        }
    }
}