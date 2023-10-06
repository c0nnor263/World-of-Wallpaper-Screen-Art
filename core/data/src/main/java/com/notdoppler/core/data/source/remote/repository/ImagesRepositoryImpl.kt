package com.notdoppler.core.data.source.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.notdoppler.core.data.source.remote.ImagePagingSource
import com.notdoppler.core.data.source.remote.ImageQueryPagingSource
import com.notdoppler.core.domain.MAX_PAGE_SIZE
import com.notdoppler.core.domain.PREFETCH_DISTANCE
import com.notdoppler.core.domain.model.FetchedImage
import com.notdoppler.core.domain.model.ImageRequestInfo
import com.notdoppler.core.domain.source.remote.repositories.ImagesRepository
import com.notdoppler.core.domain.source.remote.repositories.RemoteImageSource
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