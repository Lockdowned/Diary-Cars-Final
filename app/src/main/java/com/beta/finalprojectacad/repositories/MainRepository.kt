package com.beta.finalprojectacad.repositories

import com.beta.finalprojectacad.data.localDB.dao.CarDao
import com.beta.finalprojectacad.data.localDB.dao.RouteDao
import com.beta.finalprojectacad.data.localDB.entity.*
import com.beta.finalprojectacad.data.remoteDB.FirebaseRequests
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainRepository
@Inject constructor(
    private val carDao: CarDao,
    private val routeDao: RouteDao,
    private val firebaseRequests: FirebaseRequests,
) {

    suspend fun insertCar(car: CarRoom) {
        carDao.insertCar(car)
        firebaseRequests.insertNewCar(carDao.getAllCarsOnce().last())
    }

    suspend fun insertBrand(brand: BrandRoom) {
        carDao.insertBrand(brand)
    }

    suspend fun insertModel(model: ModelRoom) {
        carDao.insertModel(model)
    }

    suspend fun insertTransmission(transmission: TransmissionRoom) {
        carDao.insertTransmission(transmission)
    }

    suspend fun insertImg(img: ImageCarRoom) {
        carDao.insertImg(img)
        firebaseRequests.insertCarImg(img)
    }

    suspend fun insertNewRoute(route: RouteRoom) {
        routeDao.insertRoute(route)
        firebaseRequests.insertNewRoute(routeDao.getAllRoutesOnce().last())
    }

    fun getAllCars() = carDao.getAllCars()

    fun getAllImages() = carDao.getAllImages()

    fun getAllBrands() = carDao.getAllBrands()

    fun getAllModels() = carDao.getAllModels()

    fun getAllTransmissions() = carDao.getAllTransmissions()

    fun getAllRoutes() = routeDao.getAllRoutes()


    fun getAllCarsOnce(): List<CarRoom> {
        return carDao.getAllCarsOnce()
    }

    fun getAllRoutesOnce(): List<RouteRoom> {
        return routeDao.getAllRoutesOnce()
    }

    suspend fun insertRoomCar(car: CarRoom) {
        carDao.insertCar(car)
    }

    suspend fun updateRoomCar(car: CarRoom) {
        carDao.updateCar(car)
    }

    suspend fun insertRoomRoute(route: RouteRoom) = coroutineScope {
        launch {
            launch {
                firebaseRequests.saveRouteImgToScopeFromRemote(route)
            }.join()
            routeDao.insertRoute(route)
        }
    }

    fun updateRoomRoute(route: RouteRoom) {
        routeDao.updateRoute(route)
    }

    fun getAllImgOnce(): List<ImageCarRoom> {
        return carDao.getAllImgOnce()
    }

    suspend fun insertRoomImgCar(img: ImageCarRoom) = coroutineScope {
        launch {
            launch {
                firebaseRequests.saveCarImgToScopeFromRemote(img)
            }.join()
            carDao.insertImg(img)
        }
    }


    suspend fun updateRoomCarImg(img: ImageCarRoom) {
        carDao.updateCarImg(img)
    }

    fun getAllRoutesSortedByDate() = routeDao.getAllRoutesSortedByDate()
    fun getAllRoutesSortedByDistance() = routeDao.getAllRoutesSortedByDistance()
    fun getAllRoutesSortedByDuration() = routeDao.getAllRoutesSortedByDuration()
    fun getAllRoutesSortedByAvgSpeed() = routeDao.getAllRoutesSortedByAvgSpeed()
    fun getAllRoutesSortedByMaxSpeed() = routeDao.getAllRoutesSortedByMaxSpeed()

    fun firebaseAuthorizedUser() {
        firebaseRequests.setUserData()
    }
}