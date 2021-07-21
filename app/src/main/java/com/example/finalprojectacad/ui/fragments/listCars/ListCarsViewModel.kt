package com.example.finalprojectacad.ui.fragments.listCars

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.data.localDB.entity.ImageCarRoom
import com.example.finalprojectacad.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "ListCarsViewModel"

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


//    private var chosenCar: CarRoom? = null
//    val chosenCarMutableLifeData = MutableLiveData<CarRoom?>()
//
//    fun getChosenCar(): CarRoom? {
//        return chosenCar
//    }

//    fun setChosenCar(car: CarRoom?,appContext: Context) {
//        chosenCar = car
//        chosenCarMutableLifeData.value = chosenCar
//        setChosenCarIdInSharedPref(car, appContext)
//    }

//    @SuppressLint("CommitPrefEdits")
//    private fun setChosenCarIdInSharedPref(car: CarRoom?, appContext: Context) {
//        var carIdToSave = -1
//        car?.let { carNotNull ->
//            carIdToSave = carNotNull.carId!!
//        }
//        val sharedPref = appContext.getSharedPreferences("myPref", Context.MODE_PRIVATE)
//        val editor = sharedPref.edit()
//
//        editor.apply() {
//            putInt("chosenCarId", carIdToSave)
//            apply()
//        }
//        Log.d(TAG, "setChosenCarIdInSharedPref: $carIdToSave")
//    }

}