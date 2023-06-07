package com.example.resolutionai.repository


import com.example.resolutionai.data.ScannerResult
import com.example.resolutionai.utils.Consts.result
import com.example.resolutionai.utils.FirebaseUtils.database
import com.example.resolutionai.utils.FirebaseUtils.databaseRef
import com.example.resolutionai.utils.FirebaseUtils.firebaseAuth
import com.example.resolutionai.utils.FirebaseUtils.getPhonenumber
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class Repository {

    /*
    add contact
     */
    fun addResult(data: ScannerResult) {
        val randomkey = database.reference.push().key!!
        databaseRef.child(firebaseAuth.currentUser?.phoneNumber.toString())
            .child(randomkey)
            .setValue(data)
    }


    /*
    get results
     */
    fun getResults(callback: (List<ScannerResult>) -> Unit) {
        val resultList = mutableListOf<ScannerResult>()
        databaseRef.child(firebaseAuth.currentUser?.phoneNumber.toString()).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                resultList.clear()
                for (data in snapshot.children) {
                    val temp = data.getValue(ScannerResult::class.java)!!
                    resultList.add(temp)
                }

                callback(resultList)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

}