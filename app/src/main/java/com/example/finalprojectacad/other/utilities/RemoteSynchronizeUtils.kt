package com.example.finalprojectacad.other.utilities

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

object RemoteSynchronizeUtils {

    fun checkLoginUser(auth: FirebaseAuth): Boolean {
        val user = auth.currentUser
        if (user == null) {
            Log.d("Fire", "not login user")
            return false
        } else {
            Log.d("Fire", "login user: ${user.metadata}")
            return true
        }
    }
}