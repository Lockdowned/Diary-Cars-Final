package com.beta.finalprojectacad.other.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.util.Log
import androidx.core.location.LocationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.beta.finalprojectacad.data.localDB.entity.CarRoom
import com.beta.finalprojectacad.other.Constants.EMAIL_REGEX_CHECK
import com.beta.finalprojectacad.workers.SyncDatabaseWorker
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.util.regex.Pattern

private const val TAG = "FragmentsHelper"

object FragmentsHelper {

    fun showLocationPrompt(activity: Activity) {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            // Cast to a resolvable exception.
                            val resolvable: ResolvableApiException = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                activity, LocationRequest.PRIORITY_HIGH_ACCURACY
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.

                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                    }
                }
            }
        }
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    fun starWorkManagerSynchronization(applicationContext: Context) {
        val workManager = WorkManager.getInstance(applicationContext)
        val testWorker = OneTimeWorkRequestBuilder<SyncDatabaseWorker>()
            .addTag("myWorker")
            .build()
        workManager.beginWith(testWorker).enqueue()
    }

    fun getChosenCarIdAnyway(appContext: Context): Int {
        val sharedPref = appContext.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val loadedCarId = sharedPref.getInt("chosenCarId", -1)
        Log.d("SharedViewModel", "getChosenCarIdAnyway: $loadedCarId")
        return loadedCarId
    }

    @SuppressLint("CommitPrefEdits")
    fun setChosenCarIdInSharedPref(car: CarRoom?, appContext: Context) {
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
        Log.d("TAG", "setChosenCarIdInSharedPref: $carIdToSave")
    }

    fun filterCarList(
        carsList: List<CarRoom>, searchText: String
    ): List<CarRoom> {
        val filteredCarList = mutableListOf<CarRoom>()
        val correctSearchText = searchText.lowercase()
        val carRegex = ".*$correctSearchText+.*"
        val pat: Pattern = Pattern.compile(carRegex)
        for (car in carsList) {
            val carName = "${car.brandName.lowercase()} ${car.modelName.lowercase()}"
            if (pat.matcher(carName).matches()) {
                filteredCarList.add(car)
            }
        }
        return filteredCarList
    }

    fun checkCorrectEmail(emailText: String): Boolean {
        val emailRegex = EMAIL_REGEX_CHECK
        val pat: Pattern = Pattern.compile(emailRegex)
        return pat.matcher(emailText).matches()
    }
}