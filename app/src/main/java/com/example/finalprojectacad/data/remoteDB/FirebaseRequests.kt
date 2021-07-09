package com.example.finalprojectacad.data.remoteDB

import android.util.Log
import androidx.core.net.toUri
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.data.localDB.entity.ImageCarRoom
import com.example.finalprojectacad.data.localDB.entity.RouteRoom
import com.example.finalprojectacad.other.utilities.RemoteSynchronizeUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseRequests(
    private val auth: FirebaseAuth
) {

    var userDataCars: CollectionReference? = auth.currentUser?.let {
        return@let Firebase.firestore.collection("Cars user: ${auth.currentUser?.uid}")
    }
    var userDataRoutes: CollectionReference? = auth.currentUser?.let {
        return@let Firebase.firestore.collection("Routes user: ${auth.currentUser?.uid}")
    }
    var userDataImg: CollectionReference? = auth.currentUser?.let {
        return@let Firebase.firestore.collection("Images user: ${auth.currentUser?.uid}")
    }

    var userDataImgStorage: StorageReference? = auth.currentUser?.let {
        Firebase.storage.reference.child("Images user: ${auth.uid}")
    }

    fun setUserData() {
        if (RemoteSynchronizeUtils.checkLoginUser(auth)) {
            userDataCars = Firebase.firestore.collection("Cars user: ${auth.currentUser?.uid}")
            userDataRoutes = Firebase.firestore.collection("Routes user: ${auth.currentUser?.uid}")
            userDataImg = Firebase.firestore.collection("Images user: ${auth.currentUser?.uid}")
            userDataImgStorage = Firebase.storage.reference.child("Images user: ${auth.uid}")
        } else {
            userDataCars = null
            userDataRoutes = null
            userDataImg = null
            userDataImgStorage = null
        }

    }

    fun testExistence() {
        Log.d("HEY", "haha: ")
    }

    fun insertNewCar(car: CarRoom) {
        CoroutineScope(Dispatchers.IO).launch {
            userDataCars?.add(car)
        }
    }

    fun insertNewRoute(route: RouteRoom) {
        CoroutineScope(Dispatchers.IO).launch {
            userDataRoutes?.add(route)
        }
    }

    fun insertCarImg(carImg: ImageCarRoom) {
        CoroutineScope(Dispatchers.IO).launch {
            userDataImg?.add(carImg)
//            userDataImgStorage?.child("/${carImg.id}")?.putFile(carImg.imgCar.toUri())?.await()
            val a = "content://com.android.providers.media.documents/document/image%3A36"
            userDataImgStorage?.child("/${carImg.id}")?.putFile(a.toUri())?.await()
        }
    }

    fun updateCar(car: CarRoom) {
        CoroutineScope(Dispatchers.IO).launch {
            userDataCars?.let { carsUser ->
                val necessaryDoc = carsUser.whereEqualTo("id", car.carId).get().await()
                if (necessaryDoc.documents.isNotEmpty()) {
                    carsUser.document(necessaryDoc.first().id).set(car)
                }
            }
        }
    }

    fun updateRoute(route: RouteRoom) {
        CoroutineScope(Dispatchers.IO).launch {
            userDataRoutes?.let { routesUser ->
                val necessaryDoc = routesUser.whereEqualTo("routeId", route.routeId)
                    .get().await()
                if (necessaryDoc.documents.isNotEmpty()) {
                    routesUser.document(necessaryDoc.first().id).set(route)
                }
            }
        }
    }

    fun deleteCar(car: CarRoom) {
        CoroutineScope(Dispatchers.IO).launch {
            userDataCars?.let { carsUser ->
                val necessaryDoc = carsUser.whereEqualTo("id", car.carId).get().await()
                if (necessaryDoc.documents.isNotEmpty()) {
                    carsUser.document(necessaryDoc.first().id).set(car)
                }
            }
        }
    }

    suspend fun getAllCars(): List<CarRoom> {
        val list = mutableListOf<CarRoom>()
        userDataCars?.get()?.addOnSuccessListener { notDeserializedListCars ->
            for (document in notDeserializedListCars) {
                val carItem = document.toObject<CarRoom>()
                list.add(carItem)
            }
        }?.await()
        return list
    }

    suspend fun getAllRoutes(): List<RouteRoom> {
        val list = mutableListOf<RouteRoom>()
        userDataRoutes?.get()?.addOnSuccessListener { notDeserializedListRoutes ->
            for (document in notDeserializedListRoutes) {
                val routeItem = document.toObject<RouteRoom>()
                list.add(routeItem)
            }
        }?.await()
        return list
    }


}