package com.notdoppler.core.domain.model.remote

import com.notdoppler.core.domain.MAX_PAGE_SIZE
import com.notdoppler.core.domain.PREFETCH_DISTANCE
import com.notdoppler.core.domain.enums.PagingKey

data class ImageRequestInfo(
    val order: PagingKey = PagingKey.LATEST,
    val query: String? = null,
    val pageKey: Int = 1,
    val category: String = "",
    val pageSize: Int = MAX_PAGE_SIZE,
    val prefetchDistance: Int = PREFETCH_DISTANCE,
)
