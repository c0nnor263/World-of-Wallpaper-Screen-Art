package com.notdoppler.core.data.source.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import com.notdoppler.core.domain.source.remote.ApplicationPagingData
import com.notdoppler.core.domain.source.remote.ApplicationPagingDataStore
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ApplicationPagingDataStoreImpl @Inject constructor(

) : ApplicationPagingDataStore {
    override val pagingData: MutableMap<String, Pager<Int, ApplicationPagingData>>
        get() = mutableMapOf()

    override fun getPager(
        key: String,
        info: ImageRequestInfo,
        source: PagingSource<Int, ApplicationPagingData>,
    ): Pager<Int, ApplicationPagingData> {
        return pagingData.getOrPut(key) {
            Pager(
                config = PagingConfig(
                    pageSize = info.pageSize,
                    prefetchDistance = info.prefetchDistance
                ),
                pagingSourceFactory = { source }
            )
        }
    }
}