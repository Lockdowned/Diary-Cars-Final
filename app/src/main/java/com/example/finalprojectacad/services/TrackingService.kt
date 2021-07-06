package com.example.finalprojectacad.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getService
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
import com.example.finalprojectacad.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.finalprojectacad.other.Constants.ACTION_STOP_SERVICE
import com.example.finalprojectacad.other.Constants.FASTEST_LOCATION_INTERVAL
import com.example.finalprojectacad.other.Constants.LOCATION_UPDATE_INTERVAL
import com.example.finalprojectacad.other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.finalprojectacad.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.finalprojectacad.other.Constants.NOTIFICATION_ID
import com.example.finalprojectacad.other.utilities.RouteUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.inject.Inject
import kotlin.collections.ArrayList

private const val TAG = "TrackingService"

typealias Polyline = MutableList<LatLng>
typealias MutableListPolylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService: LifecycleService() {

    private var isFirstStartForegroundService = true

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private val timeRunInSeconds = MutableLiveData<Long>()

    private lateinit var curNotificationBuilder: NotificationCompat.Builder
//    private val curNotificationBuilder: NotificationCompat.Builder by lazy {
//        baseNotificationBuilder
//    }



    companion object {
        val timeRunInMillis = MutableLiveData<Long>()

        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<MutableListPolylines>()

        var isForegroundServiceStopped = false //use this in fragment is ok?

        var startDriveTime: String = ""
        var maxSpeed: Float = 0f
    }

    override fun onCreate() {
        super.onCreate()
        curNotificationBuilder = baseNotificationBuilder
        postInitialValues()

        isTracking.observe(
            this, Observer {
                updateLocationTracking(it)
                updateNotificationTrackingState(it)
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
                    stopService()
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

    private fun stopService() { //clear all data(even what we require later)
        isForegroundServiceStopped = true
        isFirstStartForegroundService = true
        pauseForegroundService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
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

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())  //here we are clear all actions(notification)
        }
        if (!isForegroundServiceStopped) { //check is in different keys
            curNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.common_google_signin_btn_icon_light, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
        }
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
            if (location.speed > maxSpeed) {
                maxSpeed = location.speed
            }
        }
    }

    private fun startForegroundService() {
        startTimer()

        val systemTime = System.currentTimeMillis()
        val formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
        startDriveTime = formatter.format(systemTime)
        Log.d(TAG, "startForegroundService: $startDriveTime")

        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
        as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        timeRunInSeconds.observe(
            this, Observer {
                if (!isForegroundServiceStopped){ //check is in different keys
                    val notification = curNotificationBuilder
                        .setContentText(RouteUtils.getFormattedTime(it * 1000L))
                    notificationManager.notify(NOTIFICATION_ID, notification.build())
                }
            }
        )
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