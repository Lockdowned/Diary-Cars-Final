package com.beta.finalprojectacad.other.utilities

import java.util.concurrent.TimeUnit

object RouteUtils {
    fun getFormattedTime(ms: Long, includeMillis: Boolean = false): String {
        var millisecond = ms
        val hours = TimeUnit.MILLISECONDS.toHours(millisecond)
        millisecond -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisecond)
        millisecond -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisecond)
        if (!includeMillis) {
            return "${if (hours < 10) "0" else ""}$hours:" +
                    "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds"
        }
        millisecond -= TimeUnit.SECONDS.toMillis(seconds)
        millisecond /= 10
        return "${if (hours < 10) "0" else ""}:$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds:" +
                "${if (millisecond < 10) "0" else ""}$millisecond"

    }
}