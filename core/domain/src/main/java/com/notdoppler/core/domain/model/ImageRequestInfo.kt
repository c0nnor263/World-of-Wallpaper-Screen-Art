package com.notdoppler.core.domain.model

import com.notdoppler.core.domain.MAX_PAGE_SIZE
import com.notdoppler.core.domain.PREFETCH_DISTANCE
import com.notdoppler.core.domain.presentation.TabOrder

data class ImageRequestInfo(
    val order: TabOrder = TabOrder.LATEST,
    val query: String? =null,
    val pageKey:Int = 1,
    val pageSize:Int = MAX_PAGE_SIZE,
    val prefetchDistance:Int = PREFETCH_DISTANCE
)
