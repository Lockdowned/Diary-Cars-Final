package com.example.finalprojectacad.data.remoteDB

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.data.localDB.entity.ImageCarRoom
import com.example.finalprojectacad.data.localDB.entity.RouteRoom
import com.example.finalprojectacad.other.utilities.RemoteSynchronizeUtils
import com.example.finalprojectacad.other.utilities.SaveImgToScopedStorage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File

private const val TAG = "FirebaseRequests"

class FirebaseRequests(
    private val auth: FirebaseAuth,
    private val appContext: Context
) {

    var userDataCars: CollectionReference? = auth.currentUser?.let {
        return@let Firebase.firestore.collection("Cars user: ${auth.currentUser?.uid}")
    }
    var userDataRoutes: CollectionReference? = auth.currentUser?.let {
        return@let Firebase.firestore.collection("Routes user: ${auth.currentUser?.uid}")
    }
    var userDataCarImg: CollectionReference? = auth.currentUser?.let {
        return@let Firebase.firestore.collection("Images user: ${auth.currentUser?.uid}")
    }

    var userDataCarImgStorage: StorageReference? = auth.currentUser?.let {
        Firebase.storage.reference.child("Images user: ${auth.uid}")
    }

    fun setUserData() {
        if (RemoteSynchronizeUtils.checkLoginUser(auth)) {
            userDataCars = Firebase.firestore.collection("Cars user: ${auth.currentUser?.uid}")
            userDataRoutes = Firebase.firestore.collection("Routes user: ${auth.currentUser?.uid}")
            userDataCarImg = Firebase.firestore.collection("Images user: ${auth.currentUser?.uid}")
            userDataCarImgStorage = Firebase.storage.reference.child("Images user: ${auth.uid}")
        } else {
            userDataCars = null
            userDataRoutes = null
            userDataCarImg = null
            userDataCarImgStorage = null
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
            userDataCarImg?.add(carImg)
            userDataCarImgStorage?.child("/${carImg.id}")?.putFile(carImg.imgCar.toUri())?.await()
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

    fun updateCarImg(img: ImageCarRoom) {
        CoroutineScope(Dispatchers.IO).launch {
            userDataCarImg?.let { imagesCarUser ->
                val necessaryDoc = imagesCarUser.whereEqualTo("id", img.id)
                    .get().await()
                if (necessaryDoc.documents.isNotEmpty()) {
                    imagesCarUser.document(necessaryDoc.first().id).set(img)
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

    suspend fun getAllCarImg(): List<ImageCarRoom> {
        val list = mutableListOf<ImageCarRoom>()
        userDataCarImg?.get()?.addOnSuccessListener { notDeserializedListImg ->
            for (document in notDeserializedListImg) {
                val imgItem = document.toObject<ImageCarRoom>()
                list.add(imgItem)
            }
        }?.await()
        return list
    }

    private var count = 1

    suspend fun saveToScopeFromRemote(img: ImageCarRoom) {

        val tempFileImg = File.createTempFile("images", "jpg")
        userDataCarImgStorage?.child("/${img.id}")?.let { it ->
            it.getFile(tempFileImg).addOnSuccessListener {
                Log.d(TAG, "saveToScopeFromRemote: ${tempFileImg.toURI()}")
                SaveImgToScopedStorage.save(appContext, img.id, tempFileImg.toUri())
                count = 1
            }.addOnFailureListener { ex ->
                Log.d(TAG, "Exception : ${ex.message}")
                if (count < 3) {
                    count++
                    GlobalScope.launch {
                        delay(2000)
                        saveToScopeFromRemote(img)
                    }
                }
            }


        }

    }


}