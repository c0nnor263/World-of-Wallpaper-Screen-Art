package com.doodle.feature.home.domain.enums

import androidx.annotation.StringRes
import com.doodle.core.domain.enums.PagingKey
import com.doodle.feature.home.R

enum class TabPage(
    @StringRes val labelRes: Int,
    val pagingKey: PagingKey
) {
    TAGS(labelRes = R.string.tab_tags, pagingKey = PagingKey.TAGS),
    LATEST(labelRes = R.string.tab_latest, pagingKey = PagingKey.LATEST),
    POPULAR(labelRes = R.string.tab_popular, pagingKey = PagingKey.POPULAR),
    EDITORS_CHOICE(labelRes = R.string.tab_editors_choice, pagingKey = PagingKey.EDITORS_CHOICE)
}
