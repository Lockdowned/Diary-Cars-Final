package com.example.finalprojectacad.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.finalprojectacad.R
import com.example.finalprojectacad.databinding.ActivityMainBinding
import com.example.finalprojectacad.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.finalprojectacad.other.utilities.FragmentsHelper
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PermissionRequest.Listener {

    private var binding: ActivityMainBinding? = null
    private lateinit var navController: NavController

    private var isFistStartApp = true //if we are change colour theme and recreate activity

    private val navHostFragment by lazy {
        supportFragmentManager
            .findFragmentById(R.id.mainNavFragment) as NavHostFragment
    }

    private val request by lazy { // request for add permission from user
        permissionsBuilder(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FragmentsHelper.starWorkManagerSynchronization(application)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding?.apply {

            navController = navHostFragment.findNavController()
            bottomNavigationBar.setupWithNavController(navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.listCarsFragment, R.id.listRoutesFragment ->
                        bottomNavigationBar.isVisible = true
                    else -> bottomNavigationBar.isVisible = false
                }
            }

            val optionsSlideInLeft = NavOptions.Builder()
                .setEnterAnim(R.anim.custom_slide_in_left)
                .setPopExitAnim(R.anim.custom_fade_out)
                .build()

            val optionsSlideInRight = NavOptions.Builder()
                .setEnterAnim(R.anim.custom_slide_in_right)
                .setPopExitAnim(R.anim.custom_fade_out)
                .build()

            bottomNavigationBar.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.listCarsFragment -> {
                        navController.navigate(R.id.listCarsFragment, null, optionsSlideInRight)
                        true
                    }
                    R.id.trackTrip -> {
                        navController.navigate(R.id.bottomSheetDialogConfCar)
                        true
                    }
                    R.id.listRoutesFragment -> {
                        navController.navigate(R.id.listRoutesFragment, null, optionsSlideInLeft)
                        true
                    }
                    else -> true
                }
            }
        }

        request.send() // asks permissions

        navigateToTrackingFragment(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragment(intent)
    }

    //function that create our app after we close app than open him from notification bar
    private fun navigateToTrackingFragment(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT && isFistStartApp) {
            navController.navigate(R.id.trackTripFragment)
        }
        isFistStartApp = false
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> Log.d(TAG, "onPermissionsResult: Denied")
            result.anyShouldShowRationale() -> Log.d(
                TAG,
                "onPermissionsResult: result.anyShouldShowRationale()"
            )
            result.allGranted() -> Log.d(TAG, "onPermissionsResult: all Allow")
        }
    }
}
