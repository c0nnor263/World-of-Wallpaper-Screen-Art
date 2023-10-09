package com.notdoppler.core.database

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import com.notdoppler.core.domain.R
import com.notdoppler.core.domain.source.local.repository.StorageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class InternalStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) : StorageManager {
    override fun saveToGallery(bitmap: Bitmap?): Boolean {
        val timestamp = System.currentTimeMillis()
        val contentResolver = context.contentResolver
        val relativePath = DIRECTORY_PICTURE +"/" + context.getString(R.string.app_name)

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.MIME_TYPE, MIME_TYPE)
            put(MediaStore.Images.Media.DATE_ADDED, timestamp)
            put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
            put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
            put(MediaStore.Images.Media.IS_PENDING, true)
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        return if (uri != null) {
            try {
                contentResolver.openOutputStream(uri)?.use { stream ->
                    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }

                values.put(MediaStore.Images.Media.IS_PENDING, false)
                contentResolver.update(uri, values, null, null)
                true
            } catch (e: Exception) {
                false
            }
        } else false

    }


    companion object {
        const val DIRECTORY_PICTURE = "Pictures"
        const val MIME_TYPE = "image/png"
    }
}