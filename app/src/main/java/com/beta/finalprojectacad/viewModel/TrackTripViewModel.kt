package com.beta.finalprojectacad.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.beta.finalprojectacad.data.localDB.entity.RouteRoom
import com.beta.finalprojectacad.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackTripViewModel
@Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {


    val allRoutesLiveData: LiveData<List<RouteRoom>> = mainRepository.getAllRoutes().asLiveData()

    fun insertNewRoute(route: RouteRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.insertNewRoute(route)
        }
    }
}