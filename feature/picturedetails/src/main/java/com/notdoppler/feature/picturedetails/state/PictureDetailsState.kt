package com.notdoppler.feature.picturedetails.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.notdoppler.core.domain.presentation.TabOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Stable
class PictureDetailsState {
    var isFavoriteEnabled by mutableStateOf(false)
    private val _tabOrder: MutableStateFlow<TabOrder?> = MutableStateFlow(null)
    val tabOrder = _tabOrder.asStateFlow()


    fun updateTabOrder(order: TabOrder) {
        _tabOrder.value = order
    }
}