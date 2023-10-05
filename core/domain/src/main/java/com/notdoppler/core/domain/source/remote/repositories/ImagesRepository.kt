package com.notdoppler.core.domain.source.remote.repositories

import androidx.paging.PagingData
import com.notdoppler.core.domain.presentation.TabCategory
import com.notdoppler.core.domain.domain.model.FetchedImage
import kotlinx.coroutines.flow.Flow

interface ImagesRepository {

    fun getImages(category:TabCategory): Flow<PagingData<FetchedImage.Hit>>
    fun searchImages(query: String): Flow<PagingData<FetchedImage.Hit>>
}