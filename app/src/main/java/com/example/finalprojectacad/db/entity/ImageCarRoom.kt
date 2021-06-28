package com.example.finalprojectacad.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_car_table")
data class ImageCarRoom(
    @ColumnInfo (name = "local_img_car") var localImgCar: String = "",
    @ColumnInfo(name = "remote_img_car") var remoteImgCar: String = "",
    @PrimaryKey val id: Int
)
