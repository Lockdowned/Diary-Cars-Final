package com.example.finalprojectacad.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.finalprojectacad.db.entity.BrandRoom
import com.example.finalprojectacad.db.entity.CarRoom
import com.example.finalprojectacad.db.entity.ModelRoom
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

    fun insertNewCar(car: CarRoom){
        viewModelScope.launch {
            mainRepository.insertCar(car)
        }
    }





}