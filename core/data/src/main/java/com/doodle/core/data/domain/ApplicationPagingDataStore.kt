package com.doodle.core.data.domain

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.doodle.core.domain.model.remote.ImageRequestInfo
import com.doodle.core.domain.model.remote.RemoteImage
import kotlinx.coroutines.flow.Flow

interface ApplicationPagingDataStore {
    val data: MutableMap<String, Flow<PagingData<RemoteImage.Hit>>>

    fun getPager(
        key: String,
        info: ImageRequestInfo,
        source: PagingSource<Int, RemoteImage.Hit>
    ): Flow<PagingData<RemoteImage.Hit>>
}
