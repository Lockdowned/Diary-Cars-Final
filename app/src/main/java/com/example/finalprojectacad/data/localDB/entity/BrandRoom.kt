package com.example.finalprojectacad.data.localDB.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brand_table")
data class BrandRoom(
    @ColumnInfo(name = "brand_name") var brandName: String = "",
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "brand_id") var brandId: Int? = null
)
