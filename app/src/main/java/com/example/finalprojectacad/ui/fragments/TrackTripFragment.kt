package com.example.finalprojectacad.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.finalprojectacad.R
import com.example.finalprojectacad.databinding.FragmentTrackTripBinding
import com.example.finalprojectacad.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.finalprojectacad.services.TrackingService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "TrackTripFragment"

@AndroidEntryPoint
class TrackTripFragment : Fragment(){

    private lateinit var binding: FragmentTrackTripBinding
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackTripBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonStartStopTracking.setOnClickListener {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }



        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        navBar.visibility = View.GONE

        lifecycleScope.launchWhenCreated {
            mapView = binding.fragmentTrackTrip
            mapView.onCreate(savedInstanceState)

            googleMap = mapView.awaitMap()
            googleMap.awaitMapLoad()

        }
    }

        override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() { // not necessarily override
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

}