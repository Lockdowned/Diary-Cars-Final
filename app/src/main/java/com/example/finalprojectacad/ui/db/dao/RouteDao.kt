package com.example.finalprojectacad.ui.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finalprojectacad.ui.db.entity.PointRouteRoom
import com.example.finalprojectacad.ui.db.entity.RouteRoom
import com.example.finalprojectacad.ui.db.entity.relations.CarWithRoutes
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPointRoute(point: PointRouteRoom)

    @Query("SELECT * FROM car_table WHERE car_id =:carId")
    fun getRoutesByCar(carId: Int): Flow<CarWithRoutes>
}