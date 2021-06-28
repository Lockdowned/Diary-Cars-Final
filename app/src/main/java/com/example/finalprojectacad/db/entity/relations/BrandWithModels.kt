package com.example.finalprojectacad.db.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.finalprojectacad.db.entity.BrandRoom
import com.example.finalprojectacad.db.entity.ModelRoom

data class BrandWithModels (
    @Embedded
    val brand: BrandRoom,
    @Relation(
        parentColumn = "brand_id",
        entityColumn = "brand_id"
    )
    val models: List<ModelRoom>
)