package com.example.finalprojectacad.ui.db.dao

import androidx.room.*
import com.example.finalprojectacad.ui.db.entity.BrandRoom
import com.example.finalprojectacad.ui.db.entity.CarRoom
import com.example.finalprojectacad.ui.db.entity.ModelRoom
import com.example.finalprojectacad.ui.db.entity.TransmissionRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: CarRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrand(brand: BrandRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: Insert)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransmission(transmission: TransmissionRoom)

    @Query("SELECT * FROM car_table")
    fun getAllCars(): Flow<List<CarRoom>>

    @Query("SELECT * FROM brand_table")
    fun getAllBrands(): Flow<List<BrandRoom>>

    @Query("SELECT * FROM transmission_table")
    fun getAllTransmissions(): Flow<List<TransmissionRoom>>

    @Query("SELECT * FROM model_table")
    fun getAllModels(): Flow<List<ModelRoom>>


//TO DO get fun one to M (brand to models)




    @Update
    suspend fun updateCar(car: CarRoom)

    @Delete
    suspend fun deleteCar(car: CarRoom)







}