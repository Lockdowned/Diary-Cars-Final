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

    private var carToEdit: CarRoom? = null

    private var chosenCar: CarRoom? = null
    val chosenCarMutableLifeData = MutableLiveData<CarRoom?>()

    fun setCarToEdit(car: CarRoom?) {
        carToEdit = car
    }

    fun getCarToEdit(): CarRoom? {
        return carToEdit
    }

    fun setChosenCar(car: CarRoom?) {
        chosenCar = car
        chosenCarMutableLifeData.value = chosenCar
    }

    fun getChosenCar(): CarRoom? {
        return chosenCar
    }
}