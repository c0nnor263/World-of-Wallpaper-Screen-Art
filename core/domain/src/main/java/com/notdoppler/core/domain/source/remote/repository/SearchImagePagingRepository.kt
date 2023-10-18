package com.notdoppler.core.domain.source.remote.repository

import androidx.paging.PagingSource
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo

interface SearchImagePagingRepository {
    fun getPagingSource(info: ImageRequestInfo): PagingSource<Int, FetchedImage.Hit>
}