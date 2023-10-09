package com.notdoppler.core.domain

import android.os.Build
import android.os.Bundle

@Suppress("DEPRECATION")
inline fun <reified T> Bundle?.getSafeParcelable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this?.getParcelable(key, T::class.java)
    } else {
        this?.getParcelable(key)
    }
}