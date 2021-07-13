package com.example.finalprojectacad.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.finalprojectacad.data.localDB.entity.RouteRoom
import com.example.finalprojectacad.databinding.FragmentTrackTripBinding
import com.example.finalprojectacad.other.Constants.ACTION_PAUSE_SERVICE
import com.example.finalprojectacad.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.finalprojectacad.other.Constants.ACTION_STOP_SERVICE
import com.example.finalprojectacad.other.utilities.RouteUtils
import com.example.finalprojectacad.other.utilities.SaveImgToScopedStorage
import com.example.finalprojectacad.services.Polyline
import com.example.finalprojectacad.services.TrackingService
import com.example.finalprojectacad.ui.activity.MainActivity
import com.example.finalprojectacad.viewModel.CarViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


private const val TAG = "TrackTripFragment"

@AndroidEntryPoint
class TrackTripFragment : Fragment(), OnMapReadyCallback {

    private var binding: FragmentTrackTripBinding? = null
    private val viewModel: CarViewModel by activityViewModels()
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    private var polylinesList = mutableListOf<Polyline>()
    private var isTracking: Boolean = false

    private var wholeDrivingTimeInMillis = 0L

    private var carId: Int = -1

    private var allRoutesList: List<RouteRoom>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackTripBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.allRoutes.observe(viewLifecycleOwner , Observer { list ->
            allRoutesList = list
        })

//        val navigation = Navigation.findNavController(view)
//        if (viewModel.getChosenCar() == null){
//            Toast.makeText(requireContext(), "Need chose a car", Toast.LENGTH_SHORT).show()
//            navigation.popBackStack()
//        }
        carId = viewModel.getChosenCarIdAnyway()
        Log.d(TAG, "onViewCreated: carId: $carId")


        binding?.apply {
            buttonStartPause.setOnClickListener {
                startOrResumeActionService()
            }

            buttonStopTracking.setOnClickListener {
                stopTracking()
            }

            val navigation = Navigation.findNavController(view)
            buttonBackToListRoute.setOnClickListener {
//                requireActivity().supportFragmentManager.popBackStack()//why crash if again track(probably know)
                navigation.popBackStack()
            }
        }



        mapView = binding!!.fragmentTrackTrip
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            googleMap = it
            addAllPolylines()
        }
        mapView.getMapAsync(this) //call onMapReady


        initializeObservers()

    }

    override fun onMapReady(gMap: GoogleMap) {
        Log.d(TAG, "onMapReady: ")

        val zoomIn = (mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(Integer.parseInt("2"))
        val zoomInOut = zoomIn.parent as View
        zoomInOut.setPadding(8, 8, 8, 150)

        val compassIn = (mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(Integer.parseInt("5"))
        val layoutParams = compassIn.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        layoutParams.setMargins(8, 150, 8, 8);

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
        CoroutineScope(Dispatchers.IO).launch {

            val startDriveTime = TrackingService.startDriveTime
            var accurateDistance = 0.0
            for (polyline in polylinesList) {
                accurateDistance += calculatePolylineLength(polyline)
            }
            val distance = accurateDistance.roundToInt()
            val duration = wholeDrivingTimeInMillis
            val avgSpeed =
                ((accurateDistance / 1000f) / (wholeDrivingTimeInMillis / 1000f / 60 / 60) * 10 / 10f).toFloat()
            val maxSpeed = TrackingService.maxSpeed
            var imgRoute = ""

//            async {
//                googleMap?.snapshot { bmp ->
//                    if (bmp == null) {
//                        val routeRoom = RouteRoom(
//                            carId,
//                            startDriveTime,
//                            distance,
//                            duration,
//                            avgSpeed,
//
//
//
//            }.await()

            googleMap?.snapshot { bmp ->
                if (bmp == null) {
                    val routeRoom = RouteRoom(
                        carId,
                        startDriveTime,
                        distance,
                        duration,
                        avgSpeed,
                        maxSpeed,
                        imgRoute
                    )
                    Log.d(TAG, "Route Room : $routeRoom")

                    viewModel.insertNewRoute(routeRoom)
                }
                bmp?.let { bitmapImg ->
                    Log.d(TAG, "saveRouteInDb: SOME bitmap")

                    val currentId = allRoutesList!!.size + 1
                    if (SaveImgToScopedStorage.saveFromBitmap(
                            requireContext(),
                            currentId,
                            bitmapImg
                        )
                    ) {
                        val act = activity as MainActivity
                        val listScopeStorageImg = act.openSavedImg()
                        val lastSavedImg =
                            listScopeStorageImg.last() // mb need find by name file
                        Log.d(TAG, "saveRouteInDb: ")

                        imgRoute = lastSavedImg.toString()

                        val routeRoom = RouteRoom(
                            carId,
                            startDriveTime,
                            distance,
                            duration,
                            avgSpeed,
                            maxSpeed,
                            imgRoute
                        )
                        Log.d(TAG, "Route Room : $routeRoom")

                        viewModel.insertNewRoute(routeRoom)
                    }

                }


            }




        }

    }

    private fun calculatePolylineLength(polyline: Polyline): Double {
        var summaryDistance = 0.0
        for (i in 0..polyline.size - 2) {
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
        binding?.apply {
            if (!isTracking && isServiceStopped) {
                buttonStartPause.text = "Start"
                buttonStopTracking.isInvisible = true
            } else if (!isTracking) {
                buttonStartPause.text = "Resume"
            } else {
                buttonStartPause.text = "Pause"
                buttonStopTracking.isInvisible = false
            }
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
                val formattedTime = RouteUtils.getFormattedTime(wholeDrivingTimeInMillis)
                Log.d(TAG, "initializeObservers: drive time : $formattedTime")

                showDurationTime(it)

                //TO DO
                //set formattedTime in fragment field
            }
        )
    }

    private fun showDistanceAndAverageSpeed() {
        var accurateDistance = 0.0
        for (polyline in polylinesList) {
            accurateDistance += calculatePolylineLength(polyline)
        }
        val distance = accurateDistance.roundToInt()
        val avgSpeed =
            ((accurateDistance / 1000f) / (wholeDrivingTimeInMillis / 1000f / 60 / 60) * 10 / 10f).toFloat()
        binding?.apply {
            textViewDistanceDriving.text = "distance: $distance m"
            textViewAverageSpeed.text = "avg speed: $avgSpeed km/h"
        }
    }

    private fun showDurationTime(durationTime: Long) {
        val formattedTime = RouteUtils.getFormattedTime(durationTime)
        binding?.textViewDurationDriving?.text = "duration $formattedTime"
    }


    private fun moveCameraToLastPoint() {
        if (polylinesList.isNotEmpty() && polylinesList.last().isNotEmpty()) {
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    polylinesList.last().last(),
                    15f
                )
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