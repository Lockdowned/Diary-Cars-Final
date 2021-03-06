package com.beta.finalprojectacad.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.beta.finalprojectacad.data.localDB.entity.CarRoom
import com.beta.finalprojectacad.data.localDB.entity.ImageCarRoom
import com.beta.finalprojectacad.repositories.MainRepository
import com.beta.finalprojectacad.ui.adaptors.CarsListAdaptor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListCarsViewModel
@Inject constructor(
    mainRepository: MainRepository
) : ViewModel() {

    var listAllCars: List<CarRoom> = listOf()
    var listAllImages: List<ImageCarRoom> = listOf()

    val allCarsLiveData: LiveData<List<CarRoom>> = mainRepository.getAllCars().asLiveData()
    val allImagesLiveData: LiveData<List<ImageCarRoom>> = mainRepository.getAllImages().asLiveData()

    val confirmCarLiveData: MutableLiveData<CarRoom?> = MutableLiveData(null)

    private var tempCarsAdaptor: CarsListAdaptor? = null

    fun getCarsRVAdaptor(): CarsListAdaptor? {
        return tempCarsAdaptor
    }

    fun setCarsRVAdaptor(adaptor: CarsListAdaptor) {
        tempCarsAdaptor = adaptor
    }
}