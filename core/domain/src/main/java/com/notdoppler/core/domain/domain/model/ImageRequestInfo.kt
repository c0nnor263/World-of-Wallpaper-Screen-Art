package com.notdoppler.core.domain.domain.model

import com.notdoppler.core.domain.presentation.TabCategory

data class ImageRequestInfo(
    val category: TabCategory = TabCategory.RECENT,
    val query: String? =null
)
