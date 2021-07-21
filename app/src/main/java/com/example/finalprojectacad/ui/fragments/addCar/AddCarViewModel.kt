package com.example.finalprojectacad.ui.fragments.addCar

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.finalprojectacad.data.localDB.entity.*
import com.example.finalprojectacad.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class AddCarViewModel
@Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    var listAllCars: List<CarRoom> = listOf() //how use between viewModel

    val allCarsLiveData: LiveData<List<CarRoom>> = mainRepository.getAllCars().asLiveData()
    val allBrandsLiveData: LiveData<List<BrandRoom>> = mainRepository.getAllBrands().asLiveData()
    val allModelsLiveData: LiveData<List<ModelRoom>> = mainRepository.getAllModels().asLiveData()
    val allTransmissionsLiveData: LiveData<List<TransmissionRoom>> =
        mainRepository.getAllTransmissions().asLiveData() // mb remove later this room table


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
        carText: String,
    ) {
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


    fun insertNewBrand(brand: BrandRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.insertBrand(brand)
        }
    }

    fun insertNewModel(model: ModelRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.insertModel(model)
        }
    }

    fun insertNewCar(car: CarRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                mainRepository.insertCar(car)
            }
        }
    }

    fun insertNewImg(img: ImageCarRoom) {
        viewModelScope.launch {
            mainRepository.insertImg(img)
        }
    }

}