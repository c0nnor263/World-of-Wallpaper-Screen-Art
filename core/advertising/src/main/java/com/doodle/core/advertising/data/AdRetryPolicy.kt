package com.doodle.core.advertising.data

import android.os.Handler
import android.os.Looper

class AdRetryPolicy(
    private var retryCount: Int = MAX_RETRY_COUNT,
    private val delay: Long = RETRY_DELAY
) {
    companion object {
        const val MAX_RETRY_COUNT = 2
        const val RETRY_DELAY = 2000L
    }

    fun retry(block: () -> Unit) {
        if (retryCount > 0) {
            retryCount--
            val runnable = Runnable {
                block()
            }
            Handler(Looper.getMainLooper())
                .postDelayed(runnable, delay)
        }
    }

    fun reset() {
        retryCount = MAX_RETRY_COUNT
    }
}