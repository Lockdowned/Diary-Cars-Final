package com.example.finalprojectacad.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.finalprojectacad.db.entity.*
import com.example.finalprojectacad.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarViewModel
@Inject constructor(
    private val mainRepository: MainRepository
): ViewModel(){

    var listAllCars: List<CarRoom> = listOf()
    var listAllImages: List<ImageCarRoom> = listOf()


    val getAllCars: LiveData<List<CarRoom>> = mainRepository.getAllCars().asLiveData()
    val getAllImages: LiveData<List<ImageCarRoom>> = mainRepository.getAllImages().asLiveData()
    val allBrands: LiveData<List<BrandRoom>> = mainRepository.getAllBrands().asLiveData()
    val allModels: LiveData<List<ModelRoom>> = mainRepository.getAllModels().asLiveData()
    fun getModelsByBrand(brandId: Int) = mainRepository.getModelsByBrand(brandId).asLiveData()
    val allTransmissions: LiveData<List<TransmissionRoom>> = mainRepository.getAllTransmissions().asLiveData()

    val allRoutesLiceData: LiveData<List<RouteRoom>> = mainRepository.getAllRoutes().asLiveData()

    private var chosenCar: CarRoom? = null

    fun insertNewCar(car: CarRoom) {
        viewModelScope.launch {
            mainRepository.insertCar(car)
        }
    }

    fun insertNewImg(img: ImageCarRoom) {
        viewModelScope.launch {
            mainRepository.insertImg(img)
        }
    }

    fun insertNewRoute(route: RouteRoom) {
        viewModelScope.launch {
            mainRepository.insertNewRoute(route)
        }
    }

    lateinit var allModelsNameBeforeChange: List<String>
    fun setAllModels(allModelsName: List<String>) {
        allModelsNameBeforeChange = allModelsName
    }

/*
change mutableListNameModels in adapter autoCompleteSetAdapter depended on brand text
 */
    fun fillCorrectModelsByCar(
        mutableListModelsName: MutableList<String>,
        listModels: List<ModelRoom>,
        listBrand: List<BrandRoom>,
        carText: String,){
        mutableListModelsName.clear()
        if (carText.isEmpty()) return
        val findIdBrand = listBrand.find { it.brandName == carText } ?: return
        val findModelsByBrand = listModels.filter {
            it.brandId == findIdBrand.brandId
        }
        if (findModelsByBrand.isEmpty()) return
        findModelsByBrand.forEach {
            mutableListModelsName.add(it.modelName)
        }
    }

    fun setChosenCar(car: CarRoom?) {
        chosenCar = car
    }

    fun getChosenCar(): CarRoom? {
        return chosenCar
    }
}