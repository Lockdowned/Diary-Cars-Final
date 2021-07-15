package com.example.finalprojectacad.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.finalprojectacad.adaptors.CarsListAdaptor
import com.example.finalprojectacad.adaptors.RouteListAdaptor
import com.example.finalprojectacad.data.localDB.entity.*
import com.example.finalprojectacad.other.enums.RouteSortType
import com.example.finalprojectacad.repositories.MainRepository
import com.example.finalprojectacad.workers.SyncDatabaseWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

private const val TAG = "TrackTripFragment"

@HiltViewModel
class CarViewModel
@Inject constructor(
    private val mainRepository: MainRepository,
) : ViewModel() {


    var listAllCars: List<CarRoom> = listOf()
    var listAllImages: List<ImageCarRoom> = listOf()

    @Inject
    lateinit var appContext: Context


    val getAllCars: LiveData<List<CarRoom>> = mainRepository.getAllCars().asLiveData()
    val getAllImages: LiveData<List<ImageCarRoom>> = mainRepository.getAllImages().asLiveData()
    val allBrands: LiveData<List<BrandRoom>> = mainRepository.getAllBrands().asLiveData()
    val allModels: LiveData<List<ModelRoom>> = mainRepository.getAllModels().asLiveData()
    val allTransmissions: LiveData<List<TransmissionRoom>> =
        mainRepository.getAllTransmissions().asLiveData()

    val allRoutes: LiveData<List<RouteRoom>> = mainRepository.getAllRoutes().asLiveData()

    private var chosenCar: CarRoom? = null

    private var carToEdit: CarRoom? = null

    fun setCarToEdit(car: CarRoom?) {
        carToEdit = car
    }

    fun getCarToEdit(): CarRoom? {
        return carToEdit
    }

    private fun setChosenCarIdInSharedPref(car: CarRoom?) {
        var carIdToSave = -1
        car?.let { carNotNull ->
            carIdToSave = carNotNull.carId!!
        }
        val sharedPref = appContext.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.apply() {
            putInt("chosenCarId", carIdToSave)
            apply()
        }
        Log.d(TAG, "setChosenCarIdInSharedPref: $carIdToSave")
    }

    fun getChosenCarIdAnyway(): Int {
        val sharedPref = appContext.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val loadedCarId = sharedPref.getInt("chosenCarId", -1)
        Log.d(TAG, "getChosenCarIdAnyway: $loadedCarId")
        return loadedCarId
    }

    fun starWorkManagerSynchronization(applicationContext: Context) {
        val workManager = WorkManager.getInstance(applicationContext)
        val testWorker = OneTimeWorkRequestBuilder<SyncDatabaseWorker>().build()
        workManager.beginWith(testWorker).enqueue()

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

    fun insertNewRoute(route: RouteRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.insertNewRoute(route)
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

    fun setChosenCar(car: CarRoom?) {
        chosenCar = car
        chosenCarMutableLifeData.value = chosenCar
        setChosenCarIdInSharedPref(car)
    }

    fun getChosenCar(): CarRoom? {
        return chosenCar
    }

    private var tempCarsAdaptor: CarsListAdaptor? = null

    fun createOrGetCarsRVAdaptor(): CarsListAdaptor {
        return tempCarsAdaptor ?: CarsListAdaptor(this)
    }

    private var tempRoutesAdaptor: RouteListAdaptor? = null

    fun createOrGetRoutesRVAdaptor(): RouteListAdaptor {
        return tempRoutesAdaptor ?: RouteListAdaptor(this)
    }

    private val routesSortedByDate = mainRepository.getAllRoutesSortedByDate().asLiveData()
    private val routesSortedByDistance = mainRepository.getAllRoutesSortedByDistance().asLiveData()
    private val routesSortedByDuration = mainRepository.getAllRoutesSortedByDuration().asLiveData()
    private val routesSortedByAvgSpeed = mainRepository.getAllRoutesSortedByAvgSpeed().asLiveData()
    private val routesSortedByMaxSpeed = mainRepository.getAllRoutesSortedByMaxSpeed().asLiveData()

    val routesSorted = MediatorLiveData<List<RouteRoom>>()

    var routeSortType = RouteSortType.DATE

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

    val chosenCarMutableLifeData = MutableLiveData<CarRoom?>()

    fun sortRoutes(routeSortType: RouteSortType) = when (routeSortType) {
        RouteSortType.DATE -> routesSortedByDate.value?.let { routesSorted.value = it }
        RouteSortType.DISTANCE -> routesSortedByDistance.value?.let { routesSorted.value = it }
        RouteSortType.DURATION -> routesSortedByDuration.value?.let { routesSorted.value = it }
        RouteSortType.AVG_SPEED -> routesSortedByAvgSpeed.value?.let { routesSorted.value = it }
        RouteSortType.MAX_SPEED -> routesSortedByMaxSpeed.value?.let { routesSorted.value = it }
    }.also {
        this.routeSortType = routeSortType
    }

    fun setAuthorizedUser() {
        mainRepository.firebaseAuthorizedUser()
    }
}