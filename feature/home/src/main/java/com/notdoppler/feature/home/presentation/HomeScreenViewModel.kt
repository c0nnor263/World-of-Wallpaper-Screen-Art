package com.notdoppler.feature.home.presentation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.notdoppler.core.data.domain.ApplicationPagingDataStore
import com.notdoppler.core.data.review.ReviewDataManager
import com.notdoppler.core.domain.enums.PagingKey
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import com.notdoppler.core.domain.model.remote.TagData
import com.notdoppler.core.domain.source.remote.repository.ImagePagingRepository
import com.notdoppler.core.domain.source.remote.repository.TagImageRepository
import com.notdoppler.feature.home.state.PagingKeyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val imagePagingRepository: ImagePagingRepository,
    private val applicationPagingDataStore: ApplicationPagingDataStore,
    private val tagImageRepository: TagImageRepository,
    private val reviewDataManager: ReviewDataManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState?>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    val pagingKeyState = PagingKeyState()

    private val _mapPagingData: SnapshotStateMap<PagingKey, MutableStateFlow<PagingData<FetchedImage.Hit>>?> =
        mutableStateMapOf()
    val mapPagingData: SnapshotStateMap<PagingKey, MutableStateFlow<PagingData<FetchedImage.Hit>>?> =
        _mapPagingData

    @OptIn(ExperimentalCoroutinesApi::class)
    val imagesFlow = pagingKeyState.pagingKeyState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        PagingKey.LATEST
    ).flatMapLatest { order ->
        val info = ImageRequestInfo(order = order)
        applicationPagingDataStore.getPager(
            key = order.requestValue,
            info = info,
            source = imagePagingRepository.getPagingSource(info)
        )
    }
        .distinctUntilChanged()
        .cachedIn(viewModelScope)

    fun updatePagingData(newValue: PagingData<FetchedImage.Hit>?) =
        viewModelScope.launch {
            _mapPagingData.getOrPut(pagingKeyState.pagingKeyState.value) {
                MutableStateFlow(PagingData.empty())
            }?.value = newValue ?: PagingData.empty()
        }

    fun updateUiState(uiState: UiState?) {
        _uiState.value = uiState
    }


    suspend fun getTagImages(tagList: ImmutableList<TagData>) = viewModelScope.launch {
        updateUiState(UiState.Loading)
        val asyncList = mutableListOf<Deferred<Unit>>()
        tagList.forEach { tag ->
            asyncList.add(
                async {
                    tag.image.value = tagImageRepository.getImageByTag(tag.title)
                }
            )
        }
        asyncList.awaitAll().also {
            updateUiState(null)
        }
    }

    fun requestReview(activity: ComponentActivity) = viewModelScope.launch {
        reviewDataManager.requestReviewInfo(activity)
    }


    sealed class UiState {
        data object Loading : UiState()
        data class Error(val message: String) : UiState()
    }
}