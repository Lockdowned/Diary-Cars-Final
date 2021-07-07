package com.example.finalprojectacad.data.remoteDB

import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.other.utilities.RemoteSynchronizeUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseRequests(
    private val auth: FirebaseAuth
) {


    private var userData: CollectionReference? = null

    fun setUserData() {
        if (RemoteSynchronizeUtils.checkLoginUser(auth)){
            userData = null
        } else {
            userData = Firebase.firestore.collection(auth.currentUser?.uid.toString())
        }

    }

    fun insertNewCar(car: CarRoom) {
        CoroutineScope(Dispatchers.IO).launch {
            userData?.add(car)
        }
    }
}