package com.example.finalprojectacad.other.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.workers.SyncDatabaseWorker
import java.util.regex.Pattern

private const val TAG = "FragmentsHelper"

object FragmentsHelper {

    fun starWorkManagerSynchronization(applicationContext: Context) {
        val workManager = WorkManager.getInstance(applicationContext)
        val testWorker = OneTimeWorkRequestBuilder<SyncDatabaseWorker>()
            .addTag("myWorker")
            .build()
        workManager.beginWith(testWorker).enqueue()
    }

    fun getChosenCarIdAnyway(appContext: Context): Int {
        val sharedPref = appContext.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val loadedCarId = sharedPref.getInt("chosenCarId", -1)
        Log.d("SharedViewModel", "getChosenCarIdAnyway: $loadedCarId")
        return loadedCarId
    }

    @SuppressLint("CommitPrefEdits")
    fun setChosenCarIdInSharedPref(car: CarRoom?, appContext: Context) {
        var carIdToSave = -1
        car?.let { carNotNull ->
            carIdToSave = carNotNull.carId!!
        }
        val sharedPref = appContext.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.apply() {
            putInt("chosenCarId", carIdToSave)
            apply()
        }
        Log.d("TAG", "setChosenCarIdInSharedPref: $carIdToSave")
    }

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