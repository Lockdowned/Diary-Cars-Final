package com.example.finalprojectacad.other.utilities

import com.example.finalprojectacad.data.localDB.entity.CarRoom
import java.util.regex.Pattern

object FragmentsHelper {


    fun filterCarList(
        carsList: List<CarRoom>, searchText: String
    ): List<CarRoom> {
        val filteredCarList = mutableListOf<CarRoom>()
        val correctSearchText = searchText.toLowerCase()
        val carRegex = ".*$correctSearchText+.*"
        val pat: Pattern = Pattern.compile(carRegex)
        for (car in carsList) {
            val carName = "${car.brandName.toLowerCase()} ${car.modelName.toLowerCase()}"
            if (pat.matcher(carName).matches()) {
                filteredCarList.add(car)
            }
        }
        return filteredCarList
    }


}