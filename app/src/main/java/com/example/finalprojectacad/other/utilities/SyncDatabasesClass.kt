package com.example.finalprojectacad.other.utilities

import com.example.finalprojectacad.data.remoteDB.FirebaseRequests
import com.example.finalprojectacad.repositories.MainRepository
import kotlinx.coroutines.*

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
        }


    }

    private suspend fun syncCar() {
        var carRoomList = mainRepository.getAllCarsOnce()
        var fireStoreCarList = firebaseRequests.getAllCars()

        if (fireStoreCarList.isEmpty()) {
            for (localCar in fireStoreCarList) {
                firebaseRequests.insertNewCar(localCar)
            }
        } else {
            for (remoteCar in fireStoreCarList) {
                val matchCar = carRoomList.find { it.carId == remoteCar.carId }
                if (matchCar == null) {
                    mainRepository.insertRoomCar(remoteCar)
                } else if (matchCar != remoteCar) { // need to compare for each parameter
                    mainRepository.updateRoomCar(remoteCar)
                }
            }
            carRoomList = mainRepository.getAllCarsOnce()
        }
    }

    private suspend fun syncRoute() {
        var routeRoomList = mainRepository.getAllRoutesOnce()
        var fireStoreRoutesList = firebaseRequests.getAllRoutes()

        if (fireStoreRoutesList.isEmpty()) {
            for (localRoute in fireStoreRoutesList) {
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
        }
    }

}