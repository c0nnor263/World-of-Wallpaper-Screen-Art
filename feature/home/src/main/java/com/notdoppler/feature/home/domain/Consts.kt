package com.notdoppler.feature.home.domain

import com.notdoppler.core.domain.presentation.TabOrder
import com.notdoppler.feature.home.domain.model.TabInfo

val tabInfo =
    listOf(
        TabInfo(title = "Latest", order = TabOrder.LATEST),
        TabInfo(title = "Popular", order = TabOrder.POPULAR),
    )
