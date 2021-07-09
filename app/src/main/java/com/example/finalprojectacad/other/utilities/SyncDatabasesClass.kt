package com.example.finalprojectacad.other.utilities

import android.util.Log
import com.example.finalprojectacad.data.remoteDB.FirebaseRequests
import com.example.finalprojectacad.repositories.MainRepository
import kotlinx.coroutines.*

private const val TAG = "SyncDatabasesClass"

class SyncDatabasesClass(
    private val firebaseRequests: FirebaseRequests,
    private val mainRepository: MainRepository
) {

    fun syncOnce() {
        GlobalScope.launch {
           async(Dispatchers.IO) {
                syncCar()
                syncRoute()
            }.await()
            async(Dispatchers.IO) {
                subscribeToFireStoreCar()
            }

        }


    }

    private suspend fun syncCar() {
        var carRoomList = mainRepository.getAllCarsOnce()
        var fireStoreCarList = firebaseRequests.getAllCars()

        if (fireStoreCarList.isEmpty()) {
            for (localCar in carRoomList) {
                firebaseRequests.insertNewCar(localCar)
            }
        } else {
            for (remoteCar in fireStoreCarList) {
                val matchCar = carRoomList.find { it.carId == remoteCar.carId }
                if (matchCar == null) {
                    mainRepository.insertRoomCar(remoteCar)
                } else if (matchCar.timestamp < remoteCar.timestamp) {
                    mainRepository.updateRoomCar(remoteCar)
                } else {
                    firebaseRequests.updateCar(matchCar)
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
        firebaseRequests.userDataCars?.let { userCars ->
            userCars.addSnapshotListener { value, error ->
                error?.let {
                    Log.e(TAG, "subscribeToFireStoreCar: crash")
                    return@addSnapshotListener
                }
                CoroutineScope(Dispatchers.IO).launch {
                    supervisorScope {
                        value?.let {
                            var carRoomList = mainRepository.getAllCarsOnce()
                            var updatedFireStoreCarList = firebaseRequests.getAllCars()

                            for (carRemote in updatedFireStoreCarList) {
                                val matchCar = carRoomList.find { it.carId == carRemote.carId}
                                if (matchCar == null) {
                                    mainRepository.insertRoomCar(carRemote)
                                } else if (matchCar.timestamp < carRemote.timestamp) {
                                    mainRepository.updateRoomCar(carRemote)
                                } else {
                                    firebaseRequests.updateCar(matchCar)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun subscribeToFireStoreRouts() {
        firebaseRequests.userDataRoutes?.let { userRoutes ->
            userRoutes.addSnapshotListener { value, error ->
                error?.let {
                    Log.e(TAG, "subscribeToFireStoreRoutes: crash")
                    return@addSnapshotListener
                }
                CoroutineScope(Dispatchers.IO).launch {
                    supervisorScope {
                        value?.let {
                            var routeRoomList = mainRepository.getAllRoutesOnce()
                            var updatedFireStoreRouteList = firebaseRequests.getAllRoutes()

                            for (routeRemote in updatedFireStoreRouteList) {
                                val matchCar = routeRoomList.find { it.carId == routeRemote.carId}
                                if (matchCar == null) {
                                    mainRepository.insertRoomRoute(routeRemote)
                                } else if (matchCar != routeRemote) {
                                    mainRepository.insertRoomRoute(routeRemote)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun syncCarImg() {

    }

    private suspend fun syncRouteImg() {

    }

}