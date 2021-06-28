package com.example.finalprojectacad.repositories

import com.example.finalprojectacad.db.dao.CarDao
import com.example.finalprojectacad.db.entity.*
import javax.inject.Inject

class MainRepository
@Inject constructor(
    private val carDao: CarDao
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

    fun getAllCars() = carDao.getAllCars()

    fun getAllBrands() = carDao.getAllBrands()

    fun getAllModels() = carDao.getAllModels()

    fun getModelsByBrand(brandId: Int) = carDao.getModelsByBrand(brandId)

    fun getAllTransmissions() = carDao.getAllTransmissions()

    suspend fun updateCar(car: CarRoom) {
        carDao.updateCar(car)
    }

    suspend fun deleteCar(car: CarRoom) {
        carDao.deleteCar(car)
    }

}