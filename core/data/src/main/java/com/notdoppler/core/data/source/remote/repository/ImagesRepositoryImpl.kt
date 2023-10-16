package com.notdoppler.core.data.source.remote.repository

import androidx.paging.PagingSource
import com.notdoppler.core.data.source.remote.paging.ImagePagingSource
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import com.notdoppler.core.domain.source.remote.repository.ImagesRepository
import com.notdoppler.core.domain.source.remote.repository.RemoteImageSource
import javax.inject.Inject

class ImagesRepositoryImpl @Inject constructor(
    private val remoteImageSource: RemoteImageSource
) : ImagesRepository {
    override fun getImagePagingSource(info: ImageRequestInfo): PagingSource<Int, FetchedImage.Hit> {
        return ImagePagingSource(remoteImageSource, info)
    }
}