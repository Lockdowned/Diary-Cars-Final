package com.beta.finalprojectacad.data.localDB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.beta.finalprojectacad.data.localDB.dao.CarDao
import com.beta.finalprojectacad.data.localDB.dao.RouteDao
import com.beta.finalprojectacad.data.localDB.entity.*

@Database(
    entities = [BrandRoom::class, CarRoom::class, ModelRoom::class, PointRouteRoom::class,
        RouteRoom::class, TransmissionRoom::class, ImageCarRoom::class],
    version = 1,
    exportSchema = false
)
abstract class AppLocalDatabase : RoomDatabase() {

    abstract fun carDao(): CarDao
    abstract fun routeDao(): RouteDao
}