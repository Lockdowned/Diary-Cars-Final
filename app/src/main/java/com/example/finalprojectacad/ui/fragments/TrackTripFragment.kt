package com.example.finalprojectacad.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.finalprojectacad.R
import com.example.finalprojectacad.databinding.FragmentTrackTripBinding
import com.example.finalprojectacad.other.Constants.ACTION_PAUSE_SERVICE
import com.example.finalprojectacad.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.finalprojectacad.other.Constants.ACTION_STOP_SERVICE
import com.example.finalprojectacad.other.utilities.Utils
import com.example.finalprojectacad.services.Polyline
import com.example.finalprojectacad.services.TrackingService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "TrackTripFragment"

@AndroidEntryPoint
class TrackTripFragment : Fragment(){

    private lateinit var binding: FragmentTrackTripBinding
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    private var polylinesList = mutableListOf<Polyline>()
    private var isTracking: Boolean = false

    private var curTimeMillis = 0L

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


        binding.buttonStartPause.setOnClickListener {
            startOrResumeActionService()
        }

        binding.buttonStopTracking.setOnClickListener {
            stopTracking()
        }



        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        navBar.visibility = View.GONE

        mapView = binding.fragmentTrackTrip
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            googleMap = it
            addAllPolylines()
        }

        initializeObservers()

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

    private fun stopTracking() {
        sendCommandToService(ACTION_STOP_SERVICE)
    }

    private fun startOrResumeActionService() {
        if (isTracking) {
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun actualizeStateButtons(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking) {
            binding.buttonStartPause.text = "Start"
            binding.buttonStopTracking.visibility = View.GONE
        } else {
            binding.buttonStartPause.text = "Pause"
            binding.buttonStopTracking.visibility = View.VISIBLE
        }
    }

    private fun initializeObservers() {
        TrackingService.isTracking.observe(
            viewLifecycleOwner, Observer {
                actualizeStateButtons(it)
            }
        )

        TrackingService.pathPoints.observe(
            viewLifecycleOwner, Observer {
                polylinesList = it
                addLatestLatLngPoint()
                moveCameraToLastPoint()
            }
        )

        TrackingService.timeRunInMillis.observe(
            viewLifecycleOwner, Observer {
                curTimeMillis = it
                val formattedTime = Utils.getFormattedTime(curTimeMillis)
                Log.d(TAG, "initializeObservers: drive time : $formattedTime")
                //TO DO
                //set formattedTime in fragment field
            }
        )
    }


    private fun moveCameraToLastPoint() {
        if (polylinesList.isNotEmpty() && polylinesList.last().isNotEmpty()) {
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    polylinesList.last().last(),
                    15f)
            )
        }
    }

    private fun addLatestLatLngPoint() {
        if (polylinesList.isNotEmpty() && polylinesList.last().size > 1) {
            val preLastLng = polylinesList.last()[polylinesList.last().lastIndex - 1]
            val lastLatLng = polylinesList.last().last()
            val polylineOptions = PolylineOptions()
                .color(Color.BLUE)
                .add(preLastLng)
                .add(lastLatLng)
            googleMap?.addPolyline(polylineOptions)
        }
    }

    private fun addAllPolylines() {
        for (polyline in polylinesList) {
            val polylineOptions = PolylineOptions()
                .color(Color.BLUE)
                .addAll(polyline)
            googleMap?.addPolyline(polylineOptions)
        }

    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

}