package com.doodle.feature.home.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import com.doodle.core.domain.enums.PagingKey
import com.doodle.core.domain.model.remote.RemoteImage
import com.doodle.core.domain.model.remote.TagData
import com.doodle.feature.home.domain.allTagCategories
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

val LocalHomePagingState = compositionLocalOf {
    HomePagingState()
}

@Stable
class HomePagingState(private val onUpdateTagList: (ImmutableList<TagData>) -> Unit = {}) {
    private val cachePagingMap =
        mutableStateMapOf<PagingKey, MutableStateFlow<PagingData<RemoteImage.Hit>>>()

    private val _keyState: MutableStateFlow<PagingKey> = MutableStateFlow(PagingKey.LATEST)
    val keyState: StateFlow<PagingKey> = _keyState.asStateFlow()

    val tagList = mutableStateListOf<TagData>().apply {
        addAll(
            allTagCategories.map { tag ->
                TagData(
                    title = tag,
                    image = mutableStateOf(null)
                )
            }
        )
    }

    var isRetrying by mutableStateOf(false)

    fun updateKey(key: PagingKey) {
        _keyState.value = key

        if (key == PagingKey.TAGS) {
            val result = tagList.any { it.image.value == null }
            if (result) onUpdateTagList(tagList.toImmutableList())
        }
    }

    fun cacheDataForCurrentKey(newValue: PagingData<RemoteImage.Hit>?) {
        val keyState = keyState.value
        cachePagingMap[keyState]?.value = newValue ?: PagingData.empty()
    }

    fun getPagingImageFlow(key: PagingKey): MutableStateFlow<PagingData<RemoteImage.Hit>> {
        return cachePagingMap.getOrPut(key) {
            MutableStateFlow(PagingData.empty())
        }
    }

    fun retry() {
        isRetrying = true
    }

    fun retryComplete() {
        isRetrying = false
    }
}
