package com.example.finalprojectacad.other.utilities

import com.example.finalprojectacad.data.localDB.entity.CarRoom
import java.util.regex.Pattern

object FragmentsHelper {


    fun filterCarList(
        carsList: List<CarRoom>, searchText: String
    ): List<CarRoom> {
        val filteredCarList = mutableListOf<CarRoom>()
        val correctSearchText = searchText.lowercase()
        val carRegex = ".*$correctSearchText+.*"
        val pat: Pattern = Pattern.compile(carRegex)
        for (car in carsList) {
            val carName = "${car.brandName.lowercase()} ${car.modelName.lowercase()}"
            if (pat.matcher(carName).matches()) {
                filteredCarList.add(car)
            }
        }
        return filteredCarList
    }

    fun checkCorrectEmail(emailText: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$"
        val pat: Pattern = Pattern.compile(emailRegex)
        return pat.matcher(emailText).matches()
    }


}