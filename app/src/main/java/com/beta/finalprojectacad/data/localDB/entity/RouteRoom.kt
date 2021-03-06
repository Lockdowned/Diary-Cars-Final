package com.beta.finalprojectacad.data.localDB.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_table")
data class RouteRoom(
    @ColumnInfo(name = "car_id") var carId: Int = 0,
    @ColumnInfo(name = "start_drive_time") var startDriveTime: Long = -1,
    var distance: Int = 0, //metres
    var duration: Long = -1,
    @ColumnInfo(name = "avg_speed") var avgSpeed: Float = 0f, //km/h
    @ColumnInfo(name = "max_speed") var maxSpeed: Float = 0f, //km/h
    @ColumnInfo (name = "img_route")var imgRoute: String = "",
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "route_id") var routeId: Int? = null
)
