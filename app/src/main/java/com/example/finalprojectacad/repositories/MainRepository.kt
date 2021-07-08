package com.example.finalprojectacad.repositories

import com.example.finalprojectacad.data.localDB.dao.CarDao
import com.example.finalprojectacad.data.localDB.dao.RouteDao
import com.example.finalprojectacad.data.localDB.entity.*
import com.example.finalprojectacad.data.remoteDB.FirebaseRequests
import javax.inject.Inject

class MainRepository
@Inject constructor(
    private val carDao: CarDao,
    private val routeDao: RouteDao,
    private val firebaseRequests: FirebaseRequests
){

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

    fun getAllRoutesOnce(): List<RouteRoom> {
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

    suspend fun updateRoomRoute(route: RouteRoom) {
        routeDao.updateRoute(route)
    }

}