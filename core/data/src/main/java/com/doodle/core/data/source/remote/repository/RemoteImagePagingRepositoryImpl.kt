package com.doodle.core.data.source.remote.repository

import androidx.paging.PagingSource
import com.doodle.core.data.source.remote.paging.RemoteImagePagingSource
import com.doodle.core.domain.model.remote.ImageRequestInfo
import com.doodle.core.domain.model.remote.RemoteImage
import com.doodle.core.domain.source.remote.RemoteImagePixabaySource
import com.doodle.core.domain.source.remote.repository.RemoteImagePagingRepository
import javax.inject.Inject

class RemoteImagePagingRepositoryImpl @Inject constructor(
    private val remoteImagePixabaySource: RemoteImagePixabaySource
) : RemoteImagePagingRepository {
    override fun getPagingSource(info: ImageRequestInfo): PagingSource<Int, RemoteImage.Hit> {
        return RemoteImagePagingSource(remoteImagePixabaySource, info)
    }
}
