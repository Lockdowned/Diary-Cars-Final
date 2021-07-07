package com.example.finalprojectacad.data.localDB.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "point_route_table")
data class PointRouteRoom(
    @ColumnInfo(name = "route_id") var routeId: Int = 0,
    var latitude: Double = 0.0,
    var longtitude: Double = 0.0,
    var time: String = "",
    var speed: Double = 0.0,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "point_route_id") var pointRouteId: Long? = null
)
