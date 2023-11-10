package com.notdoppler.feature.picturedetails.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.notdoppler.core.domain.enums.PagingKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Stable
class PictureDetailsState {
    var isFavoriteEnabled by mutableStateOf(false)
    var pagerKey by mutableStateOf("")
    private val _pagingKey: MutableStateFlow<PagingKey?> = MutableStateFlow(null)
    val tabOrder = _pagingKey.asStateFlow()


    fun updateData(order: PagingKey, key: String) {
        _pagingKey.value = order
        pagerKey = key
    }
}