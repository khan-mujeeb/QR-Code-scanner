package com.example.resolutionai.utils

import com.google.firebase.auth.FirebaseAuth

object FirebaseUtils {

    // basic ref
    var firebaseAuth = FirebaseAuth.getInstance()
    var firebaseUser = firebaseAuth.currentUser
}