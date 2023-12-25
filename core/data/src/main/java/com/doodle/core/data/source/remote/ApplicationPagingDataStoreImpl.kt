package com.doodle.core.data.source.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.doodle.core.data.domain.ApplicationPagingDataStore
import com.doodle.core.domain.di.ApplicationScope
import com.doodle.core.domain.model.remote.ImageRequestInfo
import com.doodle.core.domain.model.remote.RemoteImage
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@Singleton
class ApplicationPagingDataStoreImpl @Inject constructor(
    @ApplicationScope val applicationScope: CoroutineScope
) : ApplicationPagingDataStore {
    override val data: MutableMap<String, Flow<PagingData<RemoteImage.Hit>>> = mutableMapOf()

    override fun getPager(
        key: String,
        info: ImageRequestInfo,
        source: PagingSource<Int, RemoteImage.Hit>
    ): Flow<PagingData<RemoteImage.Hit>> {
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

        return data.getOrPut(key) {
            defaultPager.cachedIn(applicationScope)
        }
    }
}
