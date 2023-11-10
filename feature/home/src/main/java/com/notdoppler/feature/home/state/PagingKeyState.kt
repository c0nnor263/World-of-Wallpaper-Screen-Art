package com.notdoppler.feature.home.state

import androidx.compose.runtime.Stable
import com.notdoppler.core.domain.enums.PagingKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Stable
class PagingKeyState {
    private val _pagingKeyState: MutableStateFlow<PagingKey> = MutableStateFlow(PagingKey.LATEST)
    val pagingKeyState: StateFlow<PagingKey> = _pagingKeyState.asStateFlow()

    fun update(key: PagingKey) {
        _pagingKeyState.value = key
    }
}