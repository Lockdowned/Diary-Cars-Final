package com.example.finalprojectacad.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.finalprojectacad.R
import com.example.finalprojectacad.databinding.ActivityMainBinding
import com.example.finalprojectacad.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.finalprojectacad.viewModel.CarViewModel
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.IOException

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PermissionRequest.Listener {

    private val viewModel: CarViewModel by viewModels()

    lateinit var binding: ActivityMainBinding

    private val navHostFragment by lazy {
        supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
    }

    private val request by lazy { // request for add permission from user
        permissionsBuilder(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .build()
    }


    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



//        request.send()
//
//        // Construct a FusedLocationProviderClient.
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//
//
//        GlobalScope.launch {
//            while (true) {
//                getDeviceLocation()
//                delay(5000)
//            }
//        }

        navigateToTrackingFragment(intent)




        supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.apply {
           bottomNavigationBar.setupWithNavController(navController)
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragment(intent)
    }


    //function that create our app after we close app than open him from notification bar
    private fun navigateToTrackingFragment(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            navHostFragment.navController.navigate(R.id.trackTripFragment)
        }
    }

    private fun createLocationRequest() { // don't now where it need
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

    }


    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        Log.d(TAG, "HERE")

        try {

            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(this) { task ->
                if (task.isSuccessful){
                    Log.d(TAG, "getDeviceLocation: TASK GPS SUCCESSFUL: ${task.result}")
                    Log.d(TAG, "getDeviceLocation: LATLNG ${task.result.latitude}")
                    Log.d(TAG, "getDeviceLocation: LONGLNG ${task.result.longitude}")
                } else {
                    Log.d(TAG, "getDeviceLocation: TASK GPS FAILED")
                }
            }
        } catch (e: SecurityException){
            Log.e(TAG, "getDeviceLocation: ${e.message}", e)
        }


    }


    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> Log.d(TAG, "onPermissionsResult: Denied")
            result.anyShouldShowRationale() -> Log.d(TAG, "onPermissionsResult: SHOW SWTH")
            result.allGranted() -> Log.d(TAG, "onPermissionsResult: all Allow")
        }
    }

    suspend fun openSavedImg(): List<Uri> {
        return withContext(Dispatchers.IO) {
            val listUri = mutableListOf<Uri>()
            val files = filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                listUri.add(it.absolutePath.toUri())
            }
            return@withContext listUri
        }
    }


    fun saveImgCarToInternalStorage(filenameImgId: String, imgUri: Uri): Boolean{
        return try {
            val bmp =  MediaStore.Images.Media.getBitmap(this.contentResolver, imgUri) //deprecated

            openFileOutput("$filenameImgId.jpg", MODE_PRIVATE).use { stream ->
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }

            true
        } catch (e: IOException){
            e.printStackTrace()
            false
        }
    }


}
