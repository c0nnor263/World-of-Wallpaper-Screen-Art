package com.notdoppler.core.network.data.repository

import com.notdoppler.core.domain.model.FetchedImage
import com.notdoppler.core.domain.model.ImageRequestInfo
import com.notdoppler.core.domain.source.remote.repositories.RemoteImageSource
import com.notdoppler.core.network.source.PixabayService
import javax.inject.Inject

class RemoteImageSourceImpl @Inject constructor(
    private val pixabayService: PixabayService
) : RemoteImageSource {
    override suspend fun getImagesByPage(info: ImageRequestInfo): FetchedImage {
        return pixabayService.getImagesByPage(
            pageKey = info.pageKey,
            q = "",
            order = info.order.requestValue,
            perPage = info.pageSize

        )
    }

    override suspend fun getImagesByQuery(info: ImageRequestInfo): FetchedImage {
        return pixabayService.getImagesByPage(
            pageKey = info.pageKey,
            q = info.query
        )
    }
}