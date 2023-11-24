package com.notdoppler.feature.home.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.notdoppler.core.domain.enums.PagingKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Stable
class PagingKeyState {
    private val _state: MutableStateFlow<PagingKey> = MutableStateFlow(PagingKey.LATEST)
    val state: StateFlow<PagingKey> = _state.asStateFlow()

    var isRetrying by mutableStateOf(false)

    fun update(key: PagingKey) {
        _state.value = key
    }

    fun retry() {
        isRetrying = true
    }

    fun retryComplete() {
        isRetrying = false
    }
}
