package com.notdoppler.core.domain.source.local.repository

import android.net.Uri
import com.notdoppler.core.domain.model.local.StorageImageInfo

interface StorageManager {
    fun saveToGallery(info: StorageImageInfo): Boolean
    fun saveToGallery(info: StorageImageInfo, onSaved: (Uri?) -> Unit)
}