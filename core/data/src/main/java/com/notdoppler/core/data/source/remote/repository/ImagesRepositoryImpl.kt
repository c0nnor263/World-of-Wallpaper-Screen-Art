package com.notdoppler.core.data.source.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.notdoppler.core.data.source.remote.paging.ImagePagingSource
import com.notdoppler.core.data.source.remote.paging.ImageQueryPagingSource
import com.notdoppler.core.domain.model.FetchedImage
import com.notdoppler.core.domain.model.ImageRequestInfo
import com.notdoppler.core.domain.source.remote.repository.ImagesRepository
import com.notdoppler.core.domain.source.remote.repository.RemoteImageSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImagesRepositoryImpl @Inject constructor(
    private val remoteImageSource: RemoteImageSource
) : ImagesRepository {
    override fun getImages(
        info: ImageRequestInfo
    ): Flow<PagingData<FetchedImage.Hit>> {
        return Pager(
            config = PagingConfig(
                pageSize = info.pageSize,
                prefetchDistance = info.prefetchDistance
            ),
            pagingSourceFactory = {
                ImagePagingSource(remoteImageSource, info)
            }
        ).flow
    }

    override fun searchImages(info: ImageRequestInfo): Flow<PagingData<FetchedImage.Hit>> {
        return Pager(
            config = PagingConfig(
                pageSize = info.pageSize,
                prefetchDistance = info.prefetchDistance
            ),
            pagingSourceFactory = {
                ImageQueryPagingSource(remoteImageSource, info)
            }
        ).flow
    }
}