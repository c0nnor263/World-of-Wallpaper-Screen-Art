package com.notdoppler.core.domain.source.local.repository

import android.graphics.Bitmap

interface StorageManager {
    fun saveToGallery(bitmap: Bitmap?):Boolean
}