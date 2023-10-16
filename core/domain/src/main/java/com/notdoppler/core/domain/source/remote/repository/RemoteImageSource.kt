package com.notdoppler.core.domain.source.remote.repository

import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo

interface RemoteImageSource {
    suspend fun getImagesByPage(info: ImageRequestInfo): FetchedImage
    suspend fun getImagesByQuery(info: ImageRequestInfo): FetchedImage
}