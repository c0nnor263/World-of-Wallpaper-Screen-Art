package com.notdoppler.core.data.source.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.notdoppler.core.domain.di.ApplicationScope
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import com.notdoppler.core.domain.source.remote.ApplicationPagingDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ApplicationPagingDataStoreImpl @Inject constructor(
    @ApplicationScope val applicationScope: CoroutineScope,
) : ApplicationPagingDataStore {
    override val pagingData: MutableMap<String, Flow<PagingData<FetchedImage.Hit>>> = mutableMapOf()

    override fun getPager(
        key: String?,
        info: ImageRequestInfo,
        cacheEnabled: Boolean,
        source: PagingSource<Int, FetchedImage.Hit>,
    ): Flow<PagingData<FetchedImage.Hit>> {
        val defaultPager = Pager(
            config = PagingConfig(
                pageSize = info.pageSize,
                prefetchDistance = info.prefetchDistance,
            ),
            pagingSourceFactory = {
                source
            }
        ).flow
// TODO Fix this
        return if (cacheEnabled) {
            pagingData.getOrPut(key ?: info.order.requestValue) {
                defaultPager.cachedIn(applicationScope)
            }
        } else {
            pagingData[key ?: info.order.requestValue] = defaultPager
            defaultPager.cachedIn(applicationScope)
        }
    }
}