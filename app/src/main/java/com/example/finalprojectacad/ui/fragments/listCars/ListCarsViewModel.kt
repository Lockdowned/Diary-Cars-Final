package com.example.finalprojectacad.ui.fragments.listCars

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.data.localDB.entity.ImageCarRoom
import com.example.finalprojectacad.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListCarsViewModel
@Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    var listAllCars: List<CarRoom> = listOf()
    var listAllImages: List<ImageCarRoom> = listOf()

    val allCarsLiveData: LiveData<List<CarRoom>> = mainRepository.getAllCars().asLiveData()
    val allImagesLiveData: LiveData<List<ImageCarRoom>> = mainRepository.getAllImages().asLiveData()

    private var tempCarsAdaptor: CarsListAdaptor? = null

    fun getCarsRVAdaptor(): CarsListAdaptor? {
        return tempCarsAdaptor
    }

    fun setCarsRVAdaptor(adaptor: CarsListAdaptor) {
        tempCarsAdaptor = adaptor
    }
}