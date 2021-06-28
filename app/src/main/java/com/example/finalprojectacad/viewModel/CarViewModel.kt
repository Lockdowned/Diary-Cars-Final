package com.example.finalprojectacad.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.finalprojectacad.db.entity.BrandRoom
import com.example.finalprojectacad.db.entity.CarRoom
import com.example.finalprojectacad.db.entity.ModelRoom
import com.example.finalprojectacad.db.entity.TransmissionRoom
import com.example.finalprojectacad.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarViewModel
@Inject constructor(
    private val mainRepository: MainRepository
): ViewModel(){


    val getAllCars: LiveData<List<CarRoom>> = mainRepository.getAllCars().asLiveData()
    val allBrands: LiveData<List<BrandRoom>> = mainRepository.getAllBrands().asLiveData()
    val allModels: LiveData<List<ModelRoom>> = mainRepository.getAllModels().asLiveData()
    fun getModelsByBrand(brandId: Int) = mainRepository.getModelsByBrand(brandId).asLiveData()
    val allTransmissions: LiveData<List<TransmissionRoom>> = mainRepository.getAllTransmissions().asLiveData()

    fun insertNewCar(car: CarRoom){
        viewModelScope.launch {
            mainRepository.insertCar(car)
        }
    }

    lateinit var allModelsNameBeforeChange: List<String>
    fun setAllModels(allModelsName: List<String>){
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





}