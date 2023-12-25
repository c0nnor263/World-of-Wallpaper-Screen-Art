package com.doodle.core.domain.source.remote.repository

import androidx.paging.PagingSource
import com.doodle.core.domain.model.remote.ImageRequestInfo
import com.doodle.core.domain.model.remote.RemoteImage

interface RemoteImagePagingRepository {
    fun getPagingSource(info: ImageRequestInfo): PagingSource<Int, RemoteImage.Hit>
}
