package com.notdoppler.core.domain.source.remote.repositories

import com.notdoppler.core.domain.presentation.TabCategory
import com.notdoppler.core.domain.domain.model.FetchedImage

interface RemoteImageSource {
    suspend fun getImagesByPage(index:Int, category: TabCategory?): FetchedImage
    suspend fun getImagesByQuery(index:Int,query:String?): FetchedImage
}