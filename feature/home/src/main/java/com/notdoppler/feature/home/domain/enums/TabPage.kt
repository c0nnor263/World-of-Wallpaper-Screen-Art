package com.notdoppler.feature.home.domain.enums

import com.notdoppler.core.domain.enums.PagingKey

enum class TabPage(val key: PagingKey) {
    TAGS(PagingKey.TAGS),
    LATEST(PagingKey.LATEST),
    POPULAR(PagingKey.POPULAR),
}

