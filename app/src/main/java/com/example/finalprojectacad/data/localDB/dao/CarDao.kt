package com.example.finalprojectacad.data.localDB.dao

import androidx.room.*
import com.example.finalprojectacad.data.localDB.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: CarRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrand(brand: BrandRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: ModelRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransmission(transmission: TransmissionRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImg(img: ImageCarRoom)

    @Query("SELECT * FROM car_table")
    fun getAllCars(): Flow<List<CarRoom>>

    @Query("SELECT * FROM image_car_table")
    fun getAllImages(): Flow<List<ImageCarRoom>>

    @Query("SELECT * FROM brand_table")
    fun getAllBrands(): Flow<List<BrandRoom>>

    @Query("SELECT * FROM model_table")
    fun getAllModels(): Flow<List<ModelRoom>>

    @Query("SELECT * FROM model_table WHERE brand_id = :brandId")
    fun getModelsByBrand(brandId: Int): Flow<List<ModelRoom>>

    @Query("SELECT * FROM transmission_table")
    fun getAllTransmissions(): Flow<List<TransmissionRoom>>


    @Update
    suspend fun updateCar(car: CarRoom)

    @Delete
    suspend fun deleteCar(car: CarRoom)


    @Query("SELECT * FROM car_table")
    fun getAllCarsOnce(): List<CarRoom>





}