package com.example.finalprojectacad.other.utilities

import android.util.Log
import com.example.finalprojectacad.data.localDB.entity.ImageCarRoom
import com.example.finalprojectacad.data.remoteDB.FirebaseRequests
import com.example.finalprojectacad.other.Constants.DELAY_TIME_FOR_SUCCESS_SYNCHRONIZATION
import com.example.finalprojectacad.repositories.MainRepository
import kotlinx.coroutines.*

private const val TAG = "SyncDatabasesClass"

class SyncDatabasesClass(
    private val firebaseRequests: FirebaseRequests,
    private val mainRepository: MainRepository
) {

    private val localCoroutineScope = GlobalScope

    fun syncOnce() {
        localCoroutineScope.launch {
            launch {
                syncCar()
                syncRoute()
            }.join()
            launch {
                subscribeToFireStoreCar()
                subscribeToFireStoreRouts()
            }

        }
    }

    private suspend fun syncCar() {
        var carRoomList = mainRepository.getAllCarsOnce()
        var fireStoreCarList = firebaseRequests.getAllCars()

        var carImgList = mainRepository.getAllImgOnce()
        var fireStoreCarImgList = firebaseRequests.getAllCarImg()

        if (fireStoreCarList.isEmpty()) {
            for (localCar in carRoomList) {
                firebaseRequests.insertNewCar(localCar)
            }
            for (localImg in carImgList) {
                firebaseRequests.insertCarImg(localImg)
            }
        } else {
            for (remoteCar in fireStoreCarList) {
                val matchCar = carRoomList.find { it.carId == remoteCar.carId }

                var requireImgCar: ImageCarRoom?

                if (matchCar == null) {
                    mainRepository.insertRoomCar(remoteCar)
                    if (remoteCar.flagPresenceImg) {
                        requireImgCar = fireStoreCarImgList.find { it.id == remoteCar.carId }
                        requireImgCar?.let {
                            mainRepository.insertRoomImgCar(it) //need add img to scoped storage
                        }
                    }
                } else if (matchCar.timestamp < remoteCar.timestamp) {
                    mainRepository.updateRoomCar(remoteCar)
                    if (remoteCar.flagPresenceImg) {
                        requireImgCar = fireStoreCarImgList.find { it.id == remoteCar.carId }
                        requireImgCar?.let { imgCar ->
                            val requireCompareImg = carImgList.find { it.id == imgCar.id }
                            if (requireCompareImg != null && requireCompareImg.timestamp < requireImgCar!!.timestamp) {
                                mainRepository.updateRoomCarImg(requireImgCar!!)
                            }
                        }
                    }
                } else {
                    firebaseRequests.updateCar(matchCar)
                    if (matchCar.flagPresenceImg) {
                        requireImgCar = carImgList.find { it.id == matchCar.carId }
                        requireImgCar?.let {
                            firebaseRequests.updateCarImg(it)
                        }
                    }
                }
            }
            carRoomList = mainRepository.getAllCarsOnce()
            if (carRoomList.size > fireStoreCarList.size) {
                for (localCar in carRoomList) {
                    val matchCar = fireStoreCarList.find { it.carId == localCar.carId }
                    if (matchCar == null) {
                        firebaseRequests.insertNewCar(localCar)
                    }
                }
            }
        }
    }

    private suspend fun syncRoute() {
        var routeRoomList = mainRepository.getAllRoutesOnce()
        var fireStoreRoutesList = firebaseRequests.getAllRoutes()

        if (fireStoreRoutesList.isEmpty()) {
            for (localRoute in routeRoomList) {
                firebaseRequests.insertNewRoute(localRoute)
            }
        } else {
            for (remoteRoute in fireStoreRoutesList) {
                val matchRoute = routeRoomList.find { it.routeId == remoteRoute.routeId }
                if (matchRoute == null) {
                    mainRepository.insertRoomRoute(remoteRoute)
                } else if (matchRoute != remoteRoute) {
                    mainRepository.updateRoomRoute(remoteRoute)
                }
            }
            routeRoomList = mainRepository.getAllRoutesOnce()
            if (routeRoomList.size > fireStoreRoutesList.size) {
                for (localRoute in routeRoomList) {
                    val matchRoute = fireStoreRoutesList.find { it.routeId == localRoute.routeId }
                    if (matchRoute == null) {
                        firebaseRequests.insertNewRoute(localRoute)
                    }
                }
            }
        }
    }


    private suspend fun subscribeToFireStoreCar() {
        coroutineScope {
            firebaseRequests.userDataCars?.let { userCars ->
                userCars.addSnapshotListener { value, error ->
                    error?.let {
                        Log.e(TAG, "subscribeToFireStoreCar: crash")
                        return@addSnapshotListener
                    }
                    Log.d(TAG, "subscribeToFireStoreCar: triggered snapshot upper value")
                    synchronized(this) {
                        localCoroutineScope.launch {
                            delay(DELAY_TIME_FOR_SUCCESS_SYNCHRONIZATION)
                            value?.let {
                                Log.d(
                                    TAG,
                                    "subscribeToFireStoreCar: triggered snapshot upper launch"
                                )
                                Log.d(TAG, "subscribeToFireStoreCar: triggered snapshot")
                                var carRoomList = mainRepository.getAllCarsOnce()
                                var fireStoreCarList = firebaseRequests.getAllCars()

                                var carImgList = mainRepository.getAllImgOnce()
                                var fireStoreCarImgList = firebaseRequests.getAllCarImg()

                                for (remoteCar in fireStoreCarList) {
                                    val matchCar = carRoomList.find { it.carId == remoteCar.carId }

                                    var requireImgCar: ImageCarRoom?

                                    if (matchCar == null) {
                                        mainRepository.insertRoomCar(remoteCar)
                                        if (remoteCar.flagPresenceImg) {
                                            requireImgCar =
                                                fireStoreCarImgList.find { it.id == remoteCar.carId }
                                            requireImgCar?.let {
                                                val img = ImageCarRoom()
                                                mainRepository.insertRoomImgCar(it)
                                            }
                                        }
                                    } else if (matchCar.timestamp < remoteCar.timestamp) {
                                        mainRepository.updateRoomCar(remoteCar)
                                        if (remoteCar.flagPresenceImg) {
                                            requireImgCar =
                                                fireStoreCarImgList.find { it.id == remoteCar.carId }
                                            requireImgCar?.let { imgCar ->
                                                val requireCompareImg =
                                                    carImgList.find { it.id == imgCar.id }
                                                if (requireCompareImg != null && requireCompareImg.timestamp < requireImgCar!!.timestamp) {
                                                    mainRepository.updateRoomCarImg(requireImgCar!!)
                                                }
                                            }
                                        }
                                    } else {
                                        firebaseRequests.updateCar(matchCar)
                                        if (matchCar.flagPresenceImg) {
                                            requireImgCar =
                                                carImgList.find { it.id == matchCar.carId }
                                            requireImgCar?.let {
                                                firebaseRequests.updateCarImg(it)
                                            }
                                        }
                                    }
                                }
                                carRoomList = mainRepository.getAllCarsOnce()
                                if (carRoomList.size > fireStoreCarList.size) {
                                    for (localCar in carRoomList) {
                                        val matchCar =
                                            fireStoreCarList.find { it.carId == localCar.carId }
                                        if (matchCar == null) {
                                            firebaseRequests.insertNewCar(localCar)
                                        }
                                    }
                                }
                                carImgList = mainRepository.getAllImgOnce()
                                if (carImgList.size > fireStoreCarImgList.size)
                                    for (localImg in carImgList) {
                                        val matchImg =
                                            fireStoreCarImgList.find { it.id == localImg.id }
                                        if (matchImg == null) {
                                            firebaseRequests.insertCarImg(localImg)
                                        }
                                    }
                            }
                        }
                    }

                }
            }
        }
    }

    private suspend fun subscribeToFireStoreRouts() {
        coroutineScope {
            firebaseRequests.userDataRoutes?.let { userRoutes ->
                userRoutes.addSnapshotListener { value, error ->
                    error?.let {
                        Log.e(TAG, "subscribeToFireStoreRoutes: crash")
                        return@addSnapshotListener
                    }
                    synchronized(this) {
                        localCoroutineScope.launch {
                            delay(DELAY_TIME_FOR_SUCCESS_SYNCHRONIZATION)
                            value?.let {
                                var routeRoomList = mainRepository.getAllRoutesOnce()
                                var updatedFireStoreRouteList = firebaseRequests.getAllRoutes()

                                for (routeRemote in updatedFireStoreRouteList) {
                                    val matchCar =
                                        routeRoomList.find { it.carId == routeRemote.carId }
                                    if (matchCar == null) {
                                        mainRepository.insertRoomRoute(routeRemote)
                                    } else if (matchCar != routeRemote) {
                                        mainRepository.insertRoomRoute(routeRemote)
                                    }
                                }
                                routeRoomList = mainRepository.getAllRoutesOnce()
                                if (routeRoomList.size > updatedFireStoreRouteList.size) {
                                    for (localRoute in routeRoomList) {
                                        val matchRoute =
                                            updatedFireStoreRouteList.find {
                                                it.routeId == localRoute.routeId
                                            }
                                        if (matchRoute == null) {
                                            firebaseRequests.insertNewRoute(localRoute)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }


}