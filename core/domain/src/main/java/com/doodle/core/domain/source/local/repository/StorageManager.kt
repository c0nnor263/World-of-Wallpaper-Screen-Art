package com.doodle.core.domain.source.local.repository

import android.net.Uri
import com.doodle.core.domain.model.local.StorageImageInfo

interface StorageManager {
    fun saveToGallery(info: StorageImageInfo): Boolean
    fun saveToGallery(info: StorageImageInfo, onSaved: (Uri?) -> Unit)
}
