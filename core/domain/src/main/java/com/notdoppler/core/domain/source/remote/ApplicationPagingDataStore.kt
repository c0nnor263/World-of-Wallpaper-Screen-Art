package com.notdoppler.core.domain.source.remote

import androidx.paging.Pager
import androidx.paging.PagingSource
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo

interface ApplicationPagingDataStore {
    val pagingData: MutableMap<String, Pager<Int, FetchedImage.Hit>>

    fun getPager(
        key: String,
        info: ImageRequestInfo,
        source: PagingSource<Int, FetchedImage.Hit>,
    ): Pager<Int, FetchedImage.Hit>
}