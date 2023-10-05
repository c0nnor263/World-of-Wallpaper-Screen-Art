package com.notdoppler.core.domain.source.remote.repositories

import androidx.paging.PagingData
import com.notdoppler.core.domain.MAX_PAGE_SIZE
import com.notdoppler.core.domain.PREFETCH_DISTANCE
import com.notdoppler.core.domain.model.FetchedImage
import com.notdoppler.core.domain.presentation.TabCategory
import kotlinx.coroutines.flow.Flow

interface ImagesRepository {

    fun getImages(
        category: TabCategory,
        pageSize: Int = MAX_PAGE_SIZE,
        prefetchDistance: Int = PREFETCH_DISTANCE
    ): Flow<PagingData<FetchedImage.Hit>>

    fun getPagerImages(
        category: TabCategory,
        pageSize: Int = MAX_PAGE_SIZE,
        prefetchDistance: Int = PREFETCH_DISTANCE,
        key:Int = 1
    ): Flow<PagingData<FetchedImage.Hit>>


    fun searchImages(query: String): Flow<PagingData<FetchedImage.Hit>>
}