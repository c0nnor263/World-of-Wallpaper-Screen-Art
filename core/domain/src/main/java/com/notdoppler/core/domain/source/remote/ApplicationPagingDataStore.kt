package com.notdoppler.core.domain.source.remote

import androidx.paging.Pager
import androidx.paging.PagingSource
import com.notdoppler.core.domain.model.remote.ImageRequestInfo

interface ApplicationPagingDataStore {
    val pagingData: MutableMap<String, Pager<Int, ApplicationPagingData>>

    fun getPager(
        key: String,
        info: ImageRequestInfo,
        source: PagingSource<Int, ApplicationPagingData>,
    ): Pager<Int, ApplicationPagingData>
}