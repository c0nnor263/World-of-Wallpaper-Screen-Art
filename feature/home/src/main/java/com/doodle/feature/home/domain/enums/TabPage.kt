package com.doodle.feature.home.domain.enums

import com.doodle.core.domain.enums.PagingKey

enum class TabPage(val key: PagingKey) {
    TAGS(PagingKey.TAGS),
    LATEST(PagingKey.LATEST),
    POPULAR(PagingKey.POPULAR),
    EDITORS_CHOICE(PagingKey.EDITORS_CHOICE)
}
