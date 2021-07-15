package com.example.finalprojectacad.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
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
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PermissionRequest.Listener {

    @Inject
    lateinit var auth: FirebaseAuth

    private val viewModel: CarViewModel by viewModels()

    private var binding: ActivityMainBinding? = null

    private val navHostFragment by lazy {
        supportFragmentManager
            .findFragmentById(R.id.mainNavFragment) as NavHostFragment
    }

    private lateinit var navController: NavController

    private val request by lazy { // request for add permission from user
        permissionsBuilder(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.starWorkManagerSynchronization(application)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding?.apply {

            navController = navHostFragment.findNavController()
            bottomNavigationBar.setupWithNavController(navController)

            navController
                .addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.listCarsFragment, R.id.listTracksFragment ->
                            bottomNavigationBar.isVisible = true
                        else -> bottomNavigationBar.isVisible = false
                    }
                }


            bottomNavigationBar.setOnItemSelectedListener { menuItem ->
                if (NavigationUI.onNavDestinationSelected(menuItem, navController)) {
                    true
                } else {
                    when (menuItem.itemId) {
                        R.id.trackTrip -> {
                            if (viewModel.getChosenCarIdAnyway() == -1) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Need chose a car",
                                    Toast.LENGTH_SHORT
                                ).show()
                                bottomNavigationBar.selectedItemId = R.id.listCarsFragment
                                true
                            } else {
                                navController.navigate(R.id.trackTripFragment)
                                true
                            }

                        }
                        else -> false
                    }
                }
            }
        }

        request.send()

        navigateToTrackingFragment(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragment(intent)
    }

    //function that create our app after we close app than open him from notification bar
    private fun navigateToTrackingFragment(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            navController.navigate(R.id.trackTripFragment)
        }
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
