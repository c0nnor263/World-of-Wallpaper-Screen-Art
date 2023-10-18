package com.notdoppler.core.domain.source.remote.repository

import androidx.paging.PagingSource
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo

interface ImagePagingRepository {
    fun getPagingSource(info: ImageRequestInfo): PagingSource<Int, FetchedImage.Hit>
}