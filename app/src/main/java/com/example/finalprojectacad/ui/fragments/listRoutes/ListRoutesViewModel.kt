package com.example.finalprojectacad.ui.fragments.listRoutes

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.data.localDB.entity.RouteRoom
import com.example.finalprojectacad.other.enums.RouteSortType
import com.example.finalprojectacad.repositories.MainRepository
import com.example.finalprojectacad.ui.fragments.listCars.CarsListAdaptor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "ListRoutesViewModel"

@HiltViewModel
class ListRoutesViewModel
@Inject constructor(
    private val mainRepository: MainRepository
): ViewModel(){

    var listAllCars: List<CarRoom> = listOf()
    val allCarsLiveData: LiveData<List<CarRoom>> = mainRepository.getAllCars().asLiveData()




    private var tempRoutesAdaptor: RouteListAdaptor? = null

    fun getRoutesRVAdaptor(): RouteListAdaptor? {
        return tempRoutesAdaptor
    }

    fun setRoutesRVAdaptor(adaptor: RouteListAdaptor) {
        tempRoutesAdaptor = adaptor
    }


//    private var chosenCar: CarRoom? = null
//    val chosenCarMutableLifeData = MutableLiveData<CarRoom?>()
//
//    fun getChosenCar(): CarRoom? {
//        return chosenCar
//    }



    var routeSortType = RouteSortType.DATE
    val routesSorted = MediatorLiveData<List<RouteRoom>>()

    private val routesSortedByDate = mainRepository.getAllRoutesSortedByDate().asLiveData()//change name nameLiveData
    private val routesSortedByDistance = mainRepository.getAllRoutesSortedByDistance().asLiveData()//change name nameLiveData
    private val routesSortedByDuration = mainRepository.getAllRoutesSortedByDuration().asLiveData()//change name nameLiveData
    private val routesSortedByAvgSpeed = mainRepository.getAllRoutesSortedByAvgSpeed().asLiveData()//change name nameLiveData
    private val routesSortedByMaxSpeed = mainRepository.getAllRoutesSortedByMaxSpeed().asLiveData()//change name nameLiveData

    init {
        routesSorted.addSource(routesSortedByDate) { result ->
            if (routeSortType == RouteSortType.DATE) {
                result?.let {
                    routesSorted.value = it
                }
            }
        }
        routesSorted.addSource(routesSortedByDistance) { result ->
            if (routeSortType == RouteSortType.DISTANCE) {
                result?.let {
                    routesSorted.value = it
                }
            }
        }
        routesSorted.addSource(routesSortedByDuration) { result ->
            if (routeSortType == RouteSortType.DURATION) {
                result?.let {
                    routesSorted.value = it
                }
            }
        }
        routesSorted.addSource(routesSortedByAvgSpeed) { result ->
            if (routeSortType == RouteSortType.AVG_SPEED) {
                result?.let {
                    routesSorted.value = it
                }
            }
        }
        routesSorted.addSource(routesSortedByMaxSpeed) { result ->
            if (routeSortType == RouteSortType.MAX_SPEED) {
                result?.let {
                    routesSorted.value = it
                }
            }
        }
    }

//    fun setChosenCar(car: CarRoom?, appContext: Context) {
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



    fun sortRoutes(routeSortType: RouteSortType) = when (routeSortType) {
        RouteSortType.DATE -> routesSortedByDate.value?.let { routesSorted.value = it }
        RouteSortType.DISTANCE -> routesSortedByDistance.value?.let { routesSorted.value = it }
        RouteSortType.DURATION -> routesSortedByDuration.value?.let { routesSorted.value = it }
        RouteSortType.AVG_SPEED -> routesSortedByAvgSpeed.value?.let { routesSorted.value = it }
        RouteSortType.MAX_SPEED -> routesSortedByMaxSpeed.value?.let { routesSorted.value = it }
    }.also {
        this.routeSortType = routeSortType
    }

}