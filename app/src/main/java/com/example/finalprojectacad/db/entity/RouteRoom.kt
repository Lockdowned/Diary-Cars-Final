package com.example.finalprojectacad.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_table")
data class RouteRoom(
    @ColumnInfo(name = "car_id") var carId: Int = 0,
    @ColumnInfo(name = "start_drive_time") var startDriveTime: String = "",
    var distance: Double = 0.0,
    var duration: Long = 0,
    @ColumnInfo(name = "avg_speed") var avgSpeed: Double = 0.0,
    @ColumnInfo(name = "max_speed") var maxSpeed: Double = 0.0,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "route_id") var routeId: Int? = null
)
