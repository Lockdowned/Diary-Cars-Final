package com.beta.finalprojectacad.other.utilities

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

object RemoteSynchronizeUtils {

    fun checkLoginUser(auth: FirebaseAuth): Boolean {
        val user = auth.currentUser
        return if (user == null) {
            Log.d("Fire", "not login user")
            false
        } else {
            Log.d("Fire", "login user: ${user.metadata}")
            true
        }
    }
}