package com.example.finalprojectacad.ui.fragments.trackTrip

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.finalprojectacad.data.localDB.entity.RouteRoom
import com.example.finalprojectacad.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "TrackTripViewModel"

@HiltViewModel
class TrackTripViewModel
@Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {


    val allRoutesLiveData: LiveData<List<RouteRoom>> = mainRepository.getAllRoutes().asLiveData()


    fun getChosenCarIdAnyway(appContext: Context): Int { //remove from viewModel(because have context)
        val sharedPref = appContext.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val loadedCarId = sharedPref.getInt("chosenCarId", -1)
        Log.d(TAG, "getChosenCarIdAnyway: $loadedCarId")
        return loadedCarId
    }

    fun insertNewRoute(route: RouteRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.insertNewRoute(route)
        }
    }

}