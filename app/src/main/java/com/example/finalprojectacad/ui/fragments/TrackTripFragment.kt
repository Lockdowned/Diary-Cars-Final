package com.example.finalprojectacad.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.finalprojectacad.R
import com.example.finalprojectacad.databinding.FragmentTrackTripBinding
import com.example.finalprojectacad.db.entity.RouteRoom
import com.example.finalprojectacad.other.Constants.ACTION_PAUSE_SERVICE
import com.example.finalprojectacad.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.finalprojectacad.other.Constants.ACTION_STOP_SERVICE
import com.example.finalprojectacad.other.utilities.Utils
import com.example.finalprojectacad.services.Polyline
import com.example.finalprojectacad.services.TrackingService
import com.example.finalprojectacad.viewModel.CarViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

private const val TAG = "TrackTripFragment"

@AndroidEntryPoint
class TrackTripFragment : Fragment(){

    private lateinit var binding: FragmentTrackTripBinding
    private val viewModel: CarViewModel by activityViewModels()
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    private var polylinesList = mutableListOf<Polyline>()
    private var isTracking: Boolean = false

    private var wholeDrivingTimeInMillis = 0L

    private var carId: Int = -1

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

        carId = arguments?.getInt("carId")!!


        binding.apply {
            buttonStartPause.setOnClickListener {
                startOrResumeActionService()
            }

            buttonStopTracking.setOnClickListener {
                stopTracking()
            }

            val navigation = Navigation.findNavController(view)
            buttonBackToListRoute.setOnClickListener {
//                requireActivity().supportFragmentManager.popBackStack()//why crash if again track(probably now)
                navigation.navigate(R.id.action_trackTripFragment_to_listTracksFragment)
            }
        }



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
        saveRouteInDb()
    }

    private fun saveRouteInDb() {
        val startDriveTime = TrackingService.startDriveTime
        var accurateDistance = 0.0
        for (polyline in polylinesList){
            accurateDistance += calculatePolylineLength(polyline)
        }
        val distance = accurateDistance.roundToInt()
        val duration = Utils.getFormattedTime(wholeDrivingTimeInMillis)
        val avgSpeed = ((accurateDistance / 1000f) / (wholeDrivingTimeInMillis / 1000f / 60 / 60) * 10 / 10f).toFloat()
        val maxSpeed = TrackingService.maxSpeed

        val routeRoom = RouteRoom(
            carId,
            startDriveTime,
            distance,
            duration,
            avgSpeed,
            maxSpeed
        )
        Log.d(TAG, "Route Room : $routeRoom")

        viewModel.insertNewRoute(routeRoom)
    }

    private fun calculatePolylineLength(polyline: Polyline): Double  {
        var summaryDistance = 0.0
        for (i in 0..polyline.size - 2){
            val firstPoint = polyline[i]
            val endPoint = polyline[i + 1]

            val result = FloatArray(1)
            Location.distanceBetween(
                firstPoint.latitude,
                firstPoint.longitude,
                endPoint.latitude,
                endPoint.longitude,
                result
            )
            summaryDistance += result.first()
        }
        return summaryDistance
    }

    private fun startOrResumeActionService() {
        if (isTracking) {
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun actualizeStateButtons(isTracking: Boolean, isServiceStopped: Boolean) {
        this.isTracking = isTracking
        if (!isTracking && isServiceStopped) {
            binding.buttonStartPause.text = "Start"
            binding.buttonStopTracking.visibility = View.INVISIBLE
        } else if (!isTracking) {
            binding.buttonStartPause.text = "Resume"
        } else {
            binding.buttonStartPause.text = "Pause"
            binding.buttonStopTracking.visibility = View.VISIBLE
        }

    }

    private fun initializeObservers() {
        TrackingService.isTracking.observe(
            viewLifecycleOwner, Observer {
                actualizeStateButtons(it, TrackingService.isForegroundServiceStopped)
            }
        )

        TrackingService.pathPoints.observe(
            viewLifecycleOwner, Observer {
                polylinesList = it
                addLatestLatLngPoint()
                moveCameraToLastPoint()
                showDistanceAndAverageSpeed()
            }
        )

        TrackingService.timeRunInMillis.observe(
            viewLifecycleOwner, Observer {
                wholeDrivingTimeInMillis = it
                val formattedTime = Utils.getFormattedTime(wholeDrivingTimeInMillis)
                Log.d(TAG, "initializeObservers: drive time : $formattedTime")

                showDurationTime(it)

                //TO DO
                //set formattedTime in fragment field
            }
        )
    }

    private fun showDistanceAndAverageSpeed() {
        var accurateDistance = 0.0
        for (polyline in polylinesList){
            accurateDistance += calculatePolylineLength(polyline)
        }
        val distance = accurateDistance.roundToInt()
        val avgSpeed = ((accurateDistance / 1000f) / (wholeDrivingTimeInMillis / 1000f / 60 / 60) * 10 / 10f).toFloat()
        binding.apply {
            textViewDistanceDriving.text = "distance: $distance m"
            textViewAverageSpeed.text = "avg speed: $avgSpeed km/h"
        }
    }

    private fun showDurationTime(durationTime: Long) {
        val formattedTime = Utils.getFormattedTime(durationTime)
        binding.textViewDurationDriving.text = "duration $formattedTime"
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