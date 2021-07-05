package com.example.finalprojectacad.repositories

import com.example.finalprojectacad.db.dao.CarDao
import com.example.finalprojectacad.db.dao.RouteDao
import com.example.finalprojectacad.db.entity.*
import javax.inject.Inject

class MainRepository
@Inject constructor(
    private val carDao: CarDao,
    private val routeDao: RouteDao
){

    suspend fun insertCar(car: CarRoom) {
        carDao.insertCar(car)
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

}