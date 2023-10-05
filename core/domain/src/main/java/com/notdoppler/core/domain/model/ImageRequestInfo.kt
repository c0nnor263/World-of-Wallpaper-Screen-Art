package com.notdoppler.core.domain.model

import com.notdoppler.core.domain.presentation.TabCategory

data class ImageRequestInfo(
    val category: TabCategory = TabCategory.RECENT,
    val query: String? =null,
    val key:Int? = null
)
