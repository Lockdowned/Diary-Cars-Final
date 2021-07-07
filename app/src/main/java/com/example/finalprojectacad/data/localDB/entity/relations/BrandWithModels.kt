package com.example.finalprojectacad.data.localDB.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.finalprojectacad.data.localDB.entity.BrandRoom
import com.example.finalprojectacad.data.localDB.entity.ModelRoom

data class BrandWithModels (
    @Embedded
    val brand: BrandRoom,
    @Relation(
        parentColumn = "brand_id",
        entityColumn = "brand_id"
    )
    val models: List<ModelRoom>
)