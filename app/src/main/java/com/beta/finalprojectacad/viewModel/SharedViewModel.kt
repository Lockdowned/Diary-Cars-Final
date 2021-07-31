package com.beta.finalprojectacad.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beta.finalprojectacad.data.localDB.entity.CarRoom
import com.beta.finalprojectacad.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

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