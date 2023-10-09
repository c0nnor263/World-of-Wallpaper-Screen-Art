package com.notdoppler.core.domain.source.remote.repository

import androidx.paging.PagingData
import com.notdoppler.core.domain.model.FetchedImage
import com.notdoppler.core.domain.model.ImageRequestInfo
import kotlinx.coroutines.flow.Flow

interface ImagesRepository {

    fun getImages(
        info: ImageRequestInfo
    ): Flow<PagingData<FetchedImage.Hit>>
    fun searchImages(  info: ImageRequestInfo): Flow<PagingData<FetchedImage.Hit>>
}