package com.beta.finalprojectacad.data.localDB.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.beta.finalprojectacad.data.localDB.entity.CarRoom
import com.beta.finalprojectacad.data.localDB.entity.RouteRoom

data class CarWithRoutes(
    @Embedded
    val car: CarRoom,
    @Relation(
        parentColumn = "car_id",
        entityColumn = "car_id"
    )
    val routes: List<RouteRoom>
)
