package com.notdoppler.core.domain.source.remote.repositories

import com.notdoppler.core.domain.model.FetchedImage
import com.notdoppler.core.domain.model.ImageRequestInfo

interface RemoteImageSource {
    suspend fun getImagesByPage(info: ImageRequestInfo): FetchedImage
    suspend fun getImagesByQuery(info: ImageRequestInfo): FetchedImage
}