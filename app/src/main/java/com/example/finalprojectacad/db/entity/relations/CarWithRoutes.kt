package com.example.finalprojectacad.db.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.finalprojectacad.db.entity.CarRoom
import com.example.finalprojectacad.db.entity.RouteRoom

data class CarWithRoutes(
    @Embedded
    val car: CarRoom,
    @Relation(
        parentColumn = "car_id",
        entityColumn = "car_id"
    )
    val routes: List<RouteRoom>
)
