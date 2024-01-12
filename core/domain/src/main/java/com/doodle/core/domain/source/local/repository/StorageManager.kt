package com.doodle.core.domain.source.local.repository

import android.net.Uri
import com.doodle.core.domain.model.local.StorageImageInfo

interface StorageManager {
    suspend fun saveToGallery(info: StorageImageInfo): Boolean
    suspend fun saveToGallery(info: StorageImageInfo, onSaved: (Uri?) -> Unit)
    fun isFileExists(uri: Uri): Boolean
}
