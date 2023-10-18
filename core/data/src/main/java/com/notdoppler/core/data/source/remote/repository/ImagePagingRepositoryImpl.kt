package com.notdoppler.core.data.source.remote.repository

import androidx.paging.PagingSource
import com.notdoppler.core.data.source.remote.paging.ImagePagingSource
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import com.notdoppler.core.domain.source.remote.RemoteImageSource
import com.notdoppler.core.domain.source.remote.repository.ImagePagingRepository
import javax.inject.Inject

class ImagePagingRepositoryImpl @Inject constructor(
    private val remoteImageSource: RemoteImageSource,
) : ImagePagingRepository {
    override fun getPagingSource(info: ImageRequestInfo): PagingSource<Int, FetchedImage.Hit> {
        return ImagePagingSource(remoteImageSource, info)
    }
}