package com.notdoppler.core.data.source.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import com.notdoppler.core.domain.source.remote.ApplicationPagingDataStore
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ApplicationPagingDataStoreImpl @Inject constructor(

) : ApplicationPagingDataStore {
    override val pagingData: MutableMap<String, Pager<Int, FetchedImage.Hit>>
        get() = mutableMapOf()

    override fun getPager(
        key: String,
        info: ImageRequestInfo,
        source: PagingSource<Int, FetchedImage.Hit>,
    ): Pager<Int, FetchedImage.Hit> {
        return Pager(
            config = PagingConfig(
                pageSize = info.pageSize,
                prefetchDistance = info.prefetchDistance,
            ),
            pagingSourceFactory = {
                source
            }
        )
    }
}