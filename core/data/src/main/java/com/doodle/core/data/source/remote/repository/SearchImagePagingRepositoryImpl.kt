package com.doodle.core.data.source.remote.repository

import androidx.paging.PagingSource
import com.doodle.core.data.source.remote.paging.SearchImagePagingSource
import com.doodle.core.domain.model.remote.ImageRequestInfo
import com.doodle.core.domain.model.remote.RemoteImage
import com.doodle.core.domain.source.remote.RemoteImagePixabaySource
import com.doodle.core.domain.source.remote.repository.SearchImagePagingRepository
import javax.inject.Inject

class SearchImagePagingRepositoryImpl @Inject constructor(
    private val remoteImagePixabaySource: RemoteImagePixabaySource
) : SearchImagePagingRepository {
    override fun getPagingSource(info: ImageRequestInfo): PagingSource<Int, RemoteImage.Hit> {
        return SearchImagePagingSource(remoteImagePixabaySource, info)
    }
}
