package com.example.finalprojectacad.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.finalprojectacad.db.dao.CarDao
import com.example.finalprojectacad.db.dao.RouteDao
import com.example.finalprojectacad.db.entity.*

@Database(
    entities = [BrandRoom::class, CarRoom::class, ModelRoom::class,
        PointRouteRoom::class, RouteRoom::class, TransmissionRoom::class],
    version = 1,
    exportSchema = false)
abstract class AppLocalDatabase: RoomDatabase() {

    abstract fun  carDao(): CarDao
    abstract fun routeDao(): RouteDao
}