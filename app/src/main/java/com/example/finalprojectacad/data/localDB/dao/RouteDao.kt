package com.example.finalprojectacad.data.localDB.dao

import androidx.room.*
import com.example.finalprojectacad.data.localDB.entity.PointRouteRoom
import com.example.finalprojectacad.data.localDB.entity.RouteRoom
import com.example.finalprojectacad.data.localDB.entity.relations.CarWithRoutes
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPointRoute(point: PointRouteRoom)

    @Query("SELECT * FROM route_table")
    fun getAllRoutes(): Flow<List<RouteRoom>>

    @Query("SELECT * FROM car_table WHERE car_id =:carId")
    fun getRoutesByCar(carId: Int): Flow<CarWithRoutes>

    @Query("SELECT * FROM route_table")
    fun getAllRoutesOnce(): List<RouteRoom>

    @Update
    fun updateRoute(route: RouteRoom)
}