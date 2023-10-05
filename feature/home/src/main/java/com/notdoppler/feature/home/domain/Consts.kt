package com.notdoppler.feature.home.domain

import com.notdoppler.core.domain.presentation.TabCategory
import com.notdoppler.feature.home.domain.model.TabInfo

val tabInfo =
    listOf(
        TabInfo(title = "RECENT", category = TabCategory.RECENT),
        TabInfo(title = "PREMIUM", category = TabCategory.PREMIUM),
        TabInfo(title = "RANDOM", category = TabCategory.RANDOM),
        TabInfo(title = "WEEKLY POPULAR", category = TabCategory.WEEKLY_POPULAR),
        TabInfo(title = "MONTHLY POPULAR", category = TabCategory.MONTHLY_POPULAR),
        TabInfo(title = "MOST POPULAR", category = TabCategory.MOST_POPULAR)
    )
