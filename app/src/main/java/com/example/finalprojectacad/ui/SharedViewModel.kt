package com.example.finalprojectacad.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.repositories.MainRepository
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
    var confirmChosenCarFlag = false

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