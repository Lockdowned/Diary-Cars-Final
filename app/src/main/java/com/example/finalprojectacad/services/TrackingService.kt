package com.example.finalprojectacad.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.finalprojectacad.R
import com.example.finalprojectacad.other.Constants.ACTION_PAUSE_SERVICE
import com.example.finalprojectacad.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.finalprojectacad.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.finalprojectacad.other.Constants.ACTION_STOP_SERVICE
import com.example.finalprojectacad.other.Constants.FASTEST_LOCATION_INTERVAL
import com.example.finalprojectacad.other.Constants.LOCATION_UPDATE_INTERVAL
import com.example.finalprojectacad.other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.finalprojectacad.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.finalprojectacad.other.Constants.NOTIFICATION_ID
import com.example.finalprojectacad.ui.activity.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "TrackingService"

typealias Polyline = MutableList<LatLng>
typealias MutableListPolylines = MutableList<Polyline>

class TrackingService: LifecycleService() {

    private var isFirstStartForegroundService = true

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val timeRunInSeconds = MutableLiveData<Long>()

    companion object {
        val timeRunInMillis = MutableLiveData<Long>()

        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<MutableListPolylines>()
    }

    override fun onCreate() {
        super.onCreate()

        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(
            this, Observer {
                updateLocationTracking(it)
            })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                ACTION_START_OR_RESUME_SERVICE  -> {
                    if(isFirstStartForegroundService){
                        Log.d(TAG, "Start service")
                        startForegroundService()
                        isFirstStartForegroundService = false
                    } else {
                        Log.d(TAG, "Resume service")
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d(TAG, "Pause service")
                    pauseForegroundService()
                }
                ACTION_STOP_SERVICE -> {
                    Log.d(TAG, "Stop service")
                }
                else -> {
                    Log.d(TAG, "onStartCommand: else")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var isTimerEnable = false
    private var lapTime = 0L// time between pause
    private var timeRun = 0L//all time from starting
    private var timeStarted = 0L//begin lap time
    private var lastSecondLapTime = 0L//end lap time

    private fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnable = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                lapTime = System.currentTimeMillis() - timeStarted

                timeRunInMillis.postValue(timeRun + lapTime)
                if (timeRunInMillis.value!! >= lastSecondLapTime + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondLapTime += 1000L
                }
                delay(50)
            }
            timeRun += lapTime
        }
    }

    private fun pauseForegroundService() {
        isTracking.postValue(false)
        isTimerEnable = false
    }

    private fun postInitialValues() {
        //postValue - use thi method if you need set value from another thread(without main)
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }


    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (true) { //set check allow permission
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates( //!!!
                    request,
                    locationCallBack,
                    Looper.getMainLooper()
                )
            } else {
                fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
            }
        }
    }

    private val locationCallBack = object : LocationCallback() { //!!!
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result.locations.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                        Log.d(TAG,
                            "NEW GPS COORDINATES ${location.latitude} & ${location.longitude}")
                    }
                }
            }
        }
    }


    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))


    private  fun addPathPoint(location: Location?) {
        location?.let {
            val positionLatLng = LatLng(
                location.latitude,
                location.longitude
            )
            pathPoints.value?.apply {
                last().add(positionLatLng)
                pathPoints.postValue(this)
            }
        }
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    private fun startForegroundService() {
        startTimer()

        addEmptyPolyline()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
        as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_account_foreground)
            .setContentTitle("Diary tracks app")
            .setContentText("00:00:00")//info in notification bar from our app
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}