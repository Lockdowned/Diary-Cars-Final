package com.example.finalprojectacad.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transmission_table")
data class TransmissionRoom(
    @ColumnInfo(name = "transmission_name") var transmissionName: String = "",
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "transmission_id") var transmissionId: Int? = null
)