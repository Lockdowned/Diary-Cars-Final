package com.beta.finalprojectacad.data.localDB.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_car_table")
data class ImageCarRoom(
    @ColumnInfo (name = "img_car") var imgCar: String = "",
    var timestamp: Long = -1,
    @PrimaryKey var id: Int = -1
)
