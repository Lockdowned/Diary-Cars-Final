package com.example.finalprojectacad.ui.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "car_table")
data class CarRoom(
    @ColumnInfo(name = "brand_id") var brandId: Int = 0,
    @ColumnInfo(name = "model_Id") var modelId: Long = 0,
    @ColumnInfo(name = "transmission_id") var transmissionId: Int = 0,
    var engine: String = "",
    var year: Int = 0,
    var mileage: Int = 0,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "car_id") var carId: Int? = null
)
