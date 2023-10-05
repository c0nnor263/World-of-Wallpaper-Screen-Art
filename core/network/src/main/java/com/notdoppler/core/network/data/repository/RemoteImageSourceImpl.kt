package com.notdoppler.core.network.data.repository

import com.notdoppler.core.domain.domain.model.FetchedImage
import com.notdoppler.core.domain.source.remote.repositories.RemoteImageSource
import com.notdoppler.core.domain.presentation.TabCategory
import com.notdoppler.core.network.source.PixabayService
import javax.inject.Inject

class RemoteImageSourceImpl @Inject constructor(
    private val pixabayService: PixabayService
) : RemoteImageSource {
    override suspend fun getImagesByPage(index: Int, category: TabCategory?): FetchedImage {
        return pixabayService.getImagesByPage(
            page = index,
            q = "",
            category = category?.name ?: ""
        )
    }

    override suspend fun getImagesByQuery(index: Int, query: String?): FetchedImage {
        return pixabayService.getImagesByPage(
            page = index,
            q = query
        )
    }
}