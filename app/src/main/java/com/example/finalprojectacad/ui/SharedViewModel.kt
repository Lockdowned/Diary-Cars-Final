package com.example.finalprojectacad.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.repositories.MainRepository
import com.example.finalprojectacad.workers.SyncDatabaseWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "SharedViewModel"

@HiltViewModel
class SharedViewModel
@Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    var listAllCars: List<CarRoom> = listOf()
    private var carToEdit: CarRoom? = null // need for add fragment

    private var chosenCar: CarRoom? = null
    val chosenCarMutableLifeData = MutableLiveData<CarRoom?>()

    fun setCarToEdit(car: CarRoom?) { // need for add fragment
        carToEdit = car
    }

    fun getCarToEdit(): CarRoom? {
        return carToEdit
    }

    fun getChosenCar(): CarRoom? {
        return chosenCar
    }

    fun setChosenCar(car: CarRoom?, appContext: Context) {
        chosenCar = car
        chosenCarMutableLifeData.value = chosenCar
        setChosenCarIdInSharedPref(car, appContext)
    }

    @SuppressLint("CommitPrefEdits")
    private fun setChosenCarIdInSharedPref(car: CarRoom?, appContext: Context) {
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
        Log.d(TAG, "setChosenCarIdInSharedPref: $carIdToSave")
    }




















    fun starWorkManagerSynchronization(applicationContext: Context) {
        val workManager = WorkManager.getInstance(applicationContext)
        val testWorker = OneTimeWorkRequestBuilder<SyncDatabaseWorker>().build()
        workManager.beginWith(testWorker).enqueue()
    }

    fun getChosenCarIdAnyway(appContext: Context): Int {
        val sharedPref = appContext.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val loadedCarId = sharedPref.getInt("chosenCarId", -1)
        Log.d("SharedViewModel", "getChosenCarIdAnyway: $loadedCarId")
        return loadedCarId
    }
}