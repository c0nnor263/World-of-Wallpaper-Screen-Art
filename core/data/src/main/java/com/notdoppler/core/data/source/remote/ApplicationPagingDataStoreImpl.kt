package com.notdoppler.core.data.source.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.notdoppler.core.data.domain.ApplicationPagingDataStore
import com.notdoppler.core.domain.di.ApplicationScope
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
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
        key: String,
        info: ImageRequestInfo,
        source: PagingSource<Int, FetchedImage.Hit>,
    ): Flow<PagingData<FetchedImage.Hit>> {
        val defaultPager = Pager(
            config = PagingConfig(
                pageSize = info.pageSize,
                prefetchDistance = info.prefetchDistance,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                source
            }
        ).flow

        return pagingData.getOrPut(key) {
            defaultPager.cachedIn(applicationScope)
        }
    }
}