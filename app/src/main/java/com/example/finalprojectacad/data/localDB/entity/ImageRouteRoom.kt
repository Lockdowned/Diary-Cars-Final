package com.example.finalprojectacad.data.localDB.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_route_room")
data class ImageRouteRoom(
    @ColumnInfo (name = "img_route")var imgRoute: String = "",
    @PrimaryKey val id: Int
)
