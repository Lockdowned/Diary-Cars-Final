package com.example.finalprojectacad.data.localDB.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "car_table")
data class CarRoom(
    @ColumnInfo(name = "brand_name") var brandName: String = "",
    @ColumnInfo(name = "model_name") var modelName: String = "",
    @ColumnInfo(name = "transmission_name") var transmissionName: String = "",
    var engine: String = "",
    var year: Int = -1,
    var mileage: Int = -1,
    var timestamp: Long = -1, //for synchronization
    @ColumnInfo(name = "flag_presence_img") var flagPresenceImg: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "car_id") var carId: Int? = null
)
