package com.doodle.core.advertising

import java.util.concurrent.TimeUnit

fun wasLoadTimeLessThanLimitHoursAgo(lastLoadTime: Long, hours: Long): Boolean {
    val timeDifference: Long = System.currentTimeMillis() - lastLoadTime
    val limitHours = TimeUnit.MILLISECONDS.toHours(hours)
    return timeDifference < limitHours
}
