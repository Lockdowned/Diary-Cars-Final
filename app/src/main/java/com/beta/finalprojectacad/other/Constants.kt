package com.beta.finalprojectacad.other

object Constants {

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val MAP_SCALE_WEIGHT = 0.25f
    const val DEFAULT_ZOOM_LEVEL = 15f

    const val REFRESH_MILLIS_TIME_NOTIFICATION = 50L

    const val DELAY_TIME_FOR_SUCCESS_SYNCHRONIZATION = 5000L

    const val MINIMAL_LIFETIME_COROUTINE = 5000L
    const val MAX_LIFETIME_COROUTINE  = 100000L

    const val EMAIL_REGEX_CHECK = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"

}