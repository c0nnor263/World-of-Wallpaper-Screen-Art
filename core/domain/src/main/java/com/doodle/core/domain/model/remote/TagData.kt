package com.doodle.core.domain.model.remote

import androidx.compose.runtime.MutableState

data class TagData(
    val title: String,
    val image: MutableState<RemoteImage.Hit?>
)
