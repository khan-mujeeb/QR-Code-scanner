package com.example.resolutionai.utils

import com.example.resolutionai.utils.Consts.result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FirebaseUtils {

    // basic ref
    var firebaseAuth = FirebaseAuth.getInstance()
    var firebaseUser = FirebaseAuth.getInstance().currentUser

    val database = Firebase.database
    val databaseRef = database.getReference(result)

    val getPhonenumber = firebaseUser?.phoneNumber.toString()
}