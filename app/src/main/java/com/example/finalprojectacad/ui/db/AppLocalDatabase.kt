package com.example.finalprojectacad.ui.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.finalprojectacad.ui.db.entity.*

@Database(
    entities = [BrandRoom::class, CarRoom::class, ModelRoom::class,
        PointRouteRoom::class, RouteRoom::class, TransmissionRoom::class],
    version = 1,
    exportSchema = false)
abstract class AppLocalDatabase: RoomDatabase() {
}