package com.notdoppler.feature.home.state

import com.notdoppler.core.domain.presentation.TabOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TabOrderState {
    private val _tabOrderState: MutableStateFlow<TabOrder> = MutableStateFlow(TabOrder.LATEST)
    val order: StateFlow<TabOrder> = _tabOrderState.asStateFlow()

    fun update(tabOrder: TabOrder) {
        _tabOrderState.value = tabOrder
    }
}