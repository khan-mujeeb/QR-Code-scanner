package com.example.resolutionai.viewmodel

import com.example.resolutionai.data.ScannerResult
import com.example.resolutionai.repository.Repository
import com.example.resolutionai.utils.FirebaseUtils.databaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ViewModel(private val repository: Repository = Repository()) :
    androidx.lifecycle.ViewModel() {


    /*
    add result
     */
    fun addResult(data: ScannerResult) {
        repository.addResult(data)
    }

    /*
    get results
    */
    fun getResults(callback: (List<ScannerResult>) -> Unit) {
        repository.getResults { task ->
            callback(task)
        }
    }
}