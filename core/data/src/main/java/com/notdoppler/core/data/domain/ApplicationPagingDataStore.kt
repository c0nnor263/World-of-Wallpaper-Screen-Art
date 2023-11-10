package com.notdoppler.core.data.domain

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import kotlinx.coroutines.flow.Flow

interface ApplicationPagingDataStore {
    val pagingData: MutableMap<String, Flow<PagingData<FetchedImage.Hit>>>

    fun getPager(
        key: String,
        info: ImageRequestInfo,
        source: PagingSource<Int, FetchedImage.Hit>,
    ): Flow<PagingData<FetchedImage.Hit>>
}