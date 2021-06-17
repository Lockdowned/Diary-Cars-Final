package com.example.finalprojectacad.ui.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "model_table")
data class ModelRoom(
    @ColumnInfo(name = "model_name") var modelName: String = "",
    @ColumnInfo(name = "brand_id") var brandId: Int = 0,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "model_id") var modelId: Int? = null
)
