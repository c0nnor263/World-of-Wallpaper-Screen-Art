package com.notdoppler.core.data.source.remote

import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import com.notdoppler.core.domain.source.remote.RemoteImageSource
import com.notdoppler.core.domain.source.remote.RetrofitPixabayService
import javax.inject.Inject

class RemoteImageSourceImpl @Inject constructor(
    private val pixabayService: RetrofitPixabayService,
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