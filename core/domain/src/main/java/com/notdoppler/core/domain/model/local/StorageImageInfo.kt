package com.notdoppler.core.domain.model.local

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable

@Immutable
data class StorageImageInfo(
    val id: Int?,
    val userId: Int?,
    val type: String?,
    val bitmap: Bitmap?,
)
