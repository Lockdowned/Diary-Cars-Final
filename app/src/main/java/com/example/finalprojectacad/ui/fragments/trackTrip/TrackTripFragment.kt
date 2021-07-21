package com.example.finalprojectacad.ui.fragments.trackTrip

import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import com.example.finalprojectacad.other.Constants.DEFAULT_ZOOM_LEVEL
import com.example.finalprojectacad.other.Constants.MAP_SCALE_WEIGHT
import com.example.finalprojectacad.other.utilities.RouteUtils
import com.example.finalprojectacad.other.utilities.SaveImgToScopedStorage
import com.example.finalprojectacad.services.Polyline
import com.example.finalprojectacad.services.TrackingService
import com.example.finalprojectacad.ui.activity.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

private const val TAG = "TrackTripFragment"

@AndroidEntryPoint
class TrackTripFragment : Fragment(), OnMapReadyCallback {

    private var binding: FragmentTrackTripBinding? = null
    private val viewModel: TrackTripViewModel by activityViewModels()
    private lateinit var mapView: MapView //deferred remove lateinit
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

        viewModel.allRoutesLiveData.observe(viewLifecycleOwner, Observer { list ->
            allRoutesList = list
        })

        carId = viewModel.getChosenCarIdAnyway(requireActivity().applicationContext)
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
                navigation.popBackStack()
            }

            spinnerMapTypePresentation.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        when (position) {
                            0 -> googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
                            1 -> googleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
                            2 -> googleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
                            3 -> googleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
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

        val zoomButtons = findZoomButtons()
        zoomButtons.setPadding(8, 8, 8, 150)

        val compassIn = findCompass()
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
        zoomToSeeWholeTrack()
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
            val avgSpeed = calculateAvgSpeed(accurateDistance)
            val maxSpeed = TrackingService.maxSpeed
                .toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat()
            var imgRoute = ""

            googleMap?.snapshot { bmp ->
                if (bmp == null) {
                    insertRouteInDB(
                        startDriveTime,
                        distance,
                        duration,
                        avgSpeed,
                        maxSpeed,
                        imgRoute
                    )
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
                        val listScopeStorageImg =
                            SaveImgToScopedStorage.openSavedImg((activity as MainActivity).applicationContext)
                        val lastSavedImg =
                            listScopeStorageImg.last()
                        imgRoute = lastSavedImg.toString()

                        insertRouteInDB(
                            startDriveTime,
                            distance,
                            duration,
                            avgSpeed,
                            maxSpeed,
                            imgRoute
                        )
                    }
                }
            }
        }
    }

    private fun insertRouteInDB(
        startDriveTime: Long,
        distance: Int,
        duration: Long,
        avgSpeed: Float,
        maxSpeed: Float,
        imgRoute: String
    ) {
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

    private fun findZoomButtons(): View {
        val zoomIn =
            (mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(
                Integer.parseInt("2")
            )
        return zoomIn.parent as View
    }

    private fun findCompass(): View {
        return (mapView.findViewById<View>(Integer.parseInt("1")).parent as View)
            .findViewById<View>(Integer.parseInt("5"))
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
            }
        )
    }

    private fun zoomToSeeWholeTrack() {
        var boundsAreEmpty = true
        val bounds = LatLngBounds.Builder()
        for (polyline in polylinesList) {
            for (pos in polyline) {
                bounds.include(pos)
                boundsAreEmpty = false
            }
        }

        if (!boundsAreEmpty) {
            googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds.build(),
                    mapView.width,
                    mapView.height,
                    (mapView.height * MAP_SCALE_WEIGHT).toInt()
                )
            )
        }

    }

    private fun showDistanceAndAverageSpeed() {
        var accurateDistance = 0.0
        for (polyline in polylinesList) {
            accurateDistance += calculatePolylineLength(polyline)
        }
        val distance = accurateDistance.roundToInt()
        val avgSpeed = calculateAvgSpeed(accurateDistance)
        binding?.apply {
            textViewDistanceDriving.text = "distance: $distance m"
            if (avgSpeed.isNaN()) {
                textViewAverageSpeed.text = "avg speed: 0 km/h"
                return
            }
            textViewAverageSpeed.text = "avg speed: $avgSpeed km/h"
        }
    }

    private fun calculateAvgSpeed(accurateDistance: Double): Float {
        val totalAvgSpeed = ((accurateDistance / 1000f) / (wholeDrivingTimeInMillis
                / 1000f / 60 / 60) * 10 / 10f)
        Log.d(TAG, "calculateAvgSpeed: $totalAvgSpeed")
        var roundAvgSpeed = 0f
        if (!totalAvgSpeed.isNaN()) {
            roundAvgSpeed = BigDecimal(totalAvgSpeed)
                .setScale(1, RoundingMode.HALF_EVEN).toFloat()
        }
        return roundAvgSpeed
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
                    DEFAULT_ZOOM_LEVEL
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