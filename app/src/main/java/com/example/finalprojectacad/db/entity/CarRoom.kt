package com.example.finalprojectacad.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "car_table")
data class CarRoom(
    @ColumnInfo(name = "brand_name") var brandName: String = "",
    @ColumnInfo(name = "model_name") var modelName: String = "",
    @ColumnInfo(name = "transmission_name") var transmissionName: String = "",
    var engine: String = "",
    var year: Int = 0,
    var mileage: Int = 0,
    @ColumnInfo(name = "img_id") var imgId: Int = -1,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "car_id") var carId: Int? = null
)
