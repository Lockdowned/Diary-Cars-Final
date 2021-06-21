package com.example.finalprojectacad.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_car_name")
data class ImageCarRoom(
    @ColumnInfo (name = "img_car") var imgCar: String = "",
    @PrimaryKey val id: Int
)
