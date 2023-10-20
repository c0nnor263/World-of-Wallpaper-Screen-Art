package com.notdoppler.feature.search.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.notdoppler.core.domain.presentation.TabOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


@Stable
class SearchQueryState {
    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    var tabOrder: TabOrder by mutableStateOf(TabOrder.POPULAR)
        private set
    var isSearching by mutableStateOf(false)


    fun updateQuery(data: String) {
        _query.value = data

    }

    fun updateTabOrder(data: TabOrder) {
        tabOrder = data
    }
}