package com.notdoppler.core.domain.model.remote

import androidx.compose.runtime.MutableState

data class TagData(
    val title: String,
    val image: MutableState<FetchedImage.Hit?>,
)