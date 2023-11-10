package com.notdoppler.feature.home.domain.model

import com.notdoppler.core.domain.enums.PagingKey

data class TabInfo(
    val title: String,
    val order: PagingKey,
)
