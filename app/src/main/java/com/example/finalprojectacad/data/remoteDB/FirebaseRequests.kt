package com.example.finalprojectacad.data.remoteDB

import android.net.Uri
import android.util.Log
import com.example.finalprojectacad.data.localDB.entity.CarRoom
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

    private var userDataCars: CollectionReference? = auth.currentUser?.let {
        return@let Firebase.firestore.collection("Cars user: ${auth.currentUser?.uid}")
    }
    private var userDataRoutes: CollectionReference? = auth.currentUser?.let {
        return@let Firebase.firestore.collection("Routes user: ${auth.currentUser?.uid}")
    }
    private var userDataImg: StorageReference? = auth.currentUser?.let {
        Firebase.storage.reference.child("Images user: ${auth.uid}")
    }

    fun setUserData() {
        if (RemoteSynchronizeUtils.checkLoginUser(auth)) {
            userDataCars = Firebase.firestore.collection("Cars user: ${auth.currentUser?.uid}")
            userDataRoutes = Firebase.firestore.collection("Routes user: ${auth.currentUser?.uid}")
        } else {
            userDataCars = null
            userDataRoutes = null
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

    suspend fun insertImg(fileName: Int, currentFile: Uri) {
        userDataImg?.let { userDataImgPath ->
            userDataImgPath.child("/$fileName").putFile(currentFile).await()
        }
    }


}