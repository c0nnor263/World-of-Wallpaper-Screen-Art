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

    private val _pagingKey: MutableStateFlow<PagingKey?> = MutableStateFlow(null)
    val pagingKey = _pagingKey.asStateFlow()

    var isFavoriteEnabled by mutableStateOf(false)
    var query by mutableStateOf("")


    fun updateData(key: PagingKey, query: String) {
        _pagingKey.value = key
        this.query = query
    }
}