package com.doodle.feature.home.domain.model

import com.doodle.core.domain.enums.PagingKey

data class TabInfo(
    val title: String,
    val order: PagingKey
)
