package com.notdoppler.core.data.source.remote.repository

import androidx.paging.PagingSource
import com.notdoppler.core.data.source.remote.paging.SearchImagePagingSource
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import com.notdoppler.core.domain.source.remote.RemoteImageSource
import com.notdoppler.core.domain.source.remote.repository.SearchImagePagingRepository
import javax.inject.Inject

class SearchImagePagingRepositoryImpl @Inject constructor(
    private val remoteImageSource: RemoteImageSource,
) : SearchImagePagingRepository {
    override fun getPagingSource(info: ImageRequestInfo): PagingSource<Int, FetchedImage.Hit> {
        return SearchImagePagingSource(remoteImageSource, info)
    }
}