package com.notdoppler.core.domain.model.remote

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.Gson
import com.notdoppler.core.domain.model.local.StorageImageInfo
import com.notdoppler.core.domain.source.remote.ApplicationPagingData
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class FetchedImage(
    val hits: List<Hit>? = null,
    val total: Int? = null,
    val totalHits: Int? = null,
) : Parcelable, ApplicationPagingData {
    override fun toString(): String {
        return Uri.encode(Gson().toJson(this))
    }

    @Keep
    @Parcelize
    data class Hit(
        val comments: Int? = null,
        val downloads: Int? = null,
        val fullHDURL: String? = null,
        val id: Int? = null,
        val imageHeight: Int? = null,
        val imageSize: Int? = null,
        val imageURL: String? = null,
        val imageWidth: Int? = null,
        val largeImageURL: String? = null,
        val likes: Int? = null,
        val pageURL: String? = null,
        val previewHeight: Int? = null,
        val previewURL: String? = null,
        val previewWidth: Int? = null,
        val tags: String? = null,
        val type: String? = null,
        val user: String? = null,
        val userImageURL: String? = null,
        val user_id: Int? = null,
        val views: Int? = null,
        val webformatHeight: Int? = null,
        val webformatURL: String? = null,
        val webformatWidth: Int? = null,
    ) : Parcelable {
        override fun toString(): String {
            return Uri.encode(Gson().toJson(this))
        }


        fun createStorageInfo(bitmap: Bitmap?): StorageImageInfo {
            return StorageImageInfo(
                id = id,
                userId = user_id,
                type = type,
                bitmap = bitmap,
            )
        }
    }
}