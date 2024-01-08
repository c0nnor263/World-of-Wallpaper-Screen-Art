package com.doodle.core.domain.enums

enum class PagingKey(val remoteOptionQuery: String = "") {
    FAVORITES("favorites"),
    TAGS("tags"),
    LATEST("latest"),
    POPULAR("popular"),
    SEARCH("search"),
    EDITORS_CHOICE("editors_choice"),
}
