package com.notdoppler.core.data.source.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.notdoppler.core.data.MAX_PAGE_SIZE
import com.notdoppler.core.data.PREFETCH_DISTANCE
import com.notdoppler.core.domain.domain.model.FetchedImage
import com.notdoppler.core.domain.domain.model.ImageRequestInfo
import com.notdoppler.core.domain.source.remote.repositories.ImagesRepository
import com.notdoppler.core.domain.source.remote.repositories.RemoteImageSource
import com.notdoppler.core.domain.presentation.TabCategory
import com.notdoppler.core.data.source.remote.ImagePagingSource
import com.notdoppler.core.data.source.remote.ImageQueryPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImagesRepositoryImpl @Inject constructor(
    private val remoteImageSource: RemoteImageSource
) : ImagesRepository {
    override fun getImages(category: TabCategory): Flow<PagingData<FetchedImage.Hit>> {
        return Pager(
            config = PagingConfig(pageSize = MAX_PAGE_SIZE, prefetchDistance = PREFETCH_DISTANCE),
            pagingSourceFactory = {
                ImagePagingSource(remoteImageSource, ImageRequestInfo(category = category))
            }
        ).flow
    }

    override fun searchImages(query: String): Flow<PagingData<FetchedImage.Hit>> {
        return Pager(
            config = PagingConfig(pageSize = MAX_PAGE_SIZE, prefetchDistance = PREFETCH_DISTANCE),
            pagingSourceFactory = {
                ImageQueryPagingSource(remoteImageSource, ImageRequestInfo(query = query))
            }
        ).flow
    }
}