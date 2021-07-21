package com.example.finalprojectacad.ui.fragments.listRoutes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.finalprojectacad.data.localDB.entity.CarRoom
import com.example.finalprojectacad.data.localDB.entity.RouteRoom
import com.example.finalprojectacad.other.enums.RouteSortType
import com.example.finalprojectacad.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "ListRoutesViewModel"

@HiltViewModel
class ListRoutesViewModel
@Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    var listAllCars: List<CarRoom> = listOf()
    val allCarsLiveData: LiveData<List<CarRoom>> = mainRepository.getAllCars().asLiveData()


    private var tempRoutesAdaptor: RouteListAdaptor? = null

    fun getRoutesRVAdaptor(): RouteListAdaptor? {
        return tempRoutesAdaptor
    }

    fun setRoutesRVAdaptor(adaptor: RouteListAdaptor) {
        tempRoutesAdaptor = adaptor
    }

    var routeSortType = RouteSortType.DATE
    val routesSorted = MediatorLiveData<List<RouteRoom>>()

    private val routesSortedByDateLiveData = mainRepository
        .getAllRoutesSortedByDate().asLiveData()
    private val routesSortedByDistanceLiveData = mainRepository
        .getAllRoutesSortedByDistance().asLiveData()
    private val routesSortedByDurationLiveData = mainRepository
        .getAllRoutesSortedByDuration().asLiveData()
    private val routesSortedByAvgSpeedLiveData = mainRepository
        .getAllRoutesSortedByAvgSpeed().asLiveData()
    private val routesSortedByMaxSpeedLiveData = mainRepository
        .getAllRoutesSortedByMaxSpeed().asLiveData()

    init {
        routesSorted.addSource(routesSortedByDateLiveData) { result ->
            if (routeSortType == RouteSortType.DATE) {
                result?.let {
                    routesSorted.value = it
                }
            }
        }
        routesSorted.addSource(routesSortedByDistanceLiveData) { result ->
            if (routeSortType == RouteSortType.DISTANCE) {
                result?.let {
                    routesSorted.value = it
                }
            }
        }
        routesSorted.addSource(routesSortedByDurationLiveData) { result ->
            if (routeSortType == RouteSortType.DURATION) {
                result?.let {
                    routesSorted.value = it
                }
            }
        }
        routesSorted.addSource(routesSortedByAvgSpeedLiveData) { result ->
            if (routeSortType == RouteSortType.AVG_SPEED) {
                result?.let {
                    routesSorted.value = it
                }
            }
        }
        routesSorted.addSource(routesSortedByMaxSpeedLiveData) { result ->
            if (routeSortType == RouteSortType.MAX_SPEED) {
                result?.let {
                    routesSorted.value = it
                }
            }
        }
    }

    fun sortRoutes(routeSortType: RouteSortType) = when (routeSortType) {
        RouteSortType.DATE -> routesSortedByDateLiveData.value?.let { routesSorted.value = it }
        RouteSortType.DISTANCE -> routesSortedByDistanceLiveData.value?.let {
            routesSorted.value = it
        }
        RouteSortType.DURATION -> routesSortedByDurationLiveData.value?.let {
            routesSorted.value = it
        }
        RouteSortType.AVG_SPEED -> routesSortedByAvgSpeedLiveData.value?.let {
            routesSorted.value = it
        }
        RouteSortType.MAX_SPEED -> routesSortedByMaxSpeedLiveData.value?.let {
            routesSorted.value = it
        }
    }.also {
        this.routeSortType = routeSortType
    }
}