package com.notdoppler.core.database

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.notdoppler.core.domain.R
import com.notdoppler.core.domain.model.local.StorageImageInfo
import com.notdoppler.core.domain.source.local.repository.StorageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class InternalStorageManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : StorageManager {
    override fun saveToGallery(info: StorageImageInfo): Boolean {
        val contentResolver = context.contentResolver
        val appName = context.getString(R.string.app_name)
        val values = createValues(appName, info)

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        return if (uri != null) {
            try {
                contentResolver.openOutputStream(uri)?.use { stream ->
                    info.bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                contentResolver.update(uri, values, null, null)
                true
            } catch (e: Exception) {
                false
            }
        } else false
    }

    override fun saveToGallery(info: StorageImageInfo, onSaved: (Uri?) -> Unit) {
        val contentResolver = context.contentResolver
        val appName = context.getString(R.string.app_name)
        val values = createValues(appName, info)

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val result = uri?.let {
            try {
                contentResolver.openOutputStream(uri)?.use { stream ->
                    info.bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                contentResolver.update(uri, values, null, null)
                uri
            } catch (e: Exception) {
                null
            }
        }
        onSaved(result)
    }


    companion object {
        private const val DIRECTORY_PICTURE = "Pictures"
        private const val MIME_TYPE = "image/png"

        fun createValues(appName: String, info: StorageImageInfo): ContentValues {
            val fileName = "${info.id}_${info.userId}"
            val timestamp = System.currentTimeMillis()
            val relativePath = "$DIRECTORY_PICTURE/$appName"

            return ContentValues().apply {
                put(MediaStore.Images.Media.MIME_TYPE, MIME_TYPE)
                put(MediaStore.Images.Media.DATE_ADDED, timestamp)
                put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
                put(MediaStore.Images.Media.IS_PENDING, true)
            }
        }
    }
}