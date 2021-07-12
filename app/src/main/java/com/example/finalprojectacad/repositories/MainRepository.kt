package com.example.finalprojectacad.repositories

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.example.finalprojectacad.data.localDB.dao.CarDao
import com.example.finalprojectacad.data.localDB.dao.RouteDao
import com.example.finalprojectacad.data.localDB.entity.*
import com.example.finalprojectacad.data.remoteDB.FirebaseRequests
import com.example.finalprojectacad.other.utilities.SaveImgToScopedStorage
import java.io.File
import javax.inject.Inject

class MainRepository
@Inject constructor(
    private val carDao: CarDao,
    private val routeDao: RouteDao,
    private val firebaseRequests: FirebaseRequests,
){

    fun testExistence() {
        Log.d("HEY", "testExistence: MainRepository true")
    }

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

    fun getModelsByBrand(brandId: Int) = carDao.getModelsByBrand(brandId)

    fun getAllTransmissions() = carDao.getAllTransmissions()

    fun getAllRoutes() = routeDao.getAllRoutes()

    suspend fun updateCar(car: CarRoom) {
        carDao.updateCar(car)
    }

    suspend fun deleteCar(car: CarRoom) {
        carDao.deleteCar(car)
    }

    fun getAllCarsOnce(): List<CarRoom> {
        return carDao.getAllCarsOnce()
    }

    suspend fun getAllRoutesOnce(): List<RouteRoom> {
        return routeDao.getAllRoutesOnce()
    }

    suspend fun insertRoomCar(car: CarRoom) {
        carDao.insertCar(car)
    }

    suspend fun updateRoomCar(car: CarRoom) {
        carDao.updateCar(car)
    }

    suspend fun insertRoomRoute(route: RouteRoom) {
        routeDao.insertRoute(route)
    }

    fun updateRoomRoute(route: RouteRoom) {
        routeDao.updateRoute(route)
    }

    fun getAllImgOnce(): List<ImageCarRoom> {
        return carDao.getAllImgOnce()
    }

    suspend fun insertRoomImgCar(img: ImageCarRoom) {
        firebaseRequests.saveToScopeFromRemote(img)
        carDao.insertImg(img)
    }


    suspend fun updateRoomCarImg(img: ImageCarRoom) {
        carDao.updateCarImg(img)
    }

    fun getAllRoutesSortedByDate() = routeDao.getAllRoutesSortedByDate()
    fun getAllRoutesSortedByDistance() = routeDao.getAllRoutesSortedByDistance()
    fun getAllRoutesSortedByDuration() = routeDao.getAllRoutesSortedByDuration()
    fun getAllRoutesSortedByAvgSpeed() = routeDao.getAllRoutesSortedByAvgSpeed()
    fun getAllRoutesSortedByMaxSpeed() = routeDao.getAllRoutesSortedByMaxSpeed()

}