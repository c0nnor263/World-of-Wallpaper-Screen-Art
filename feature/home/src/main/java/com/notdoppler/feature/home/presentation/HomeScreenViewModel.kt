package com.notdoppler.feature.home.presentation

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import com.notdoppler.core.domain.presentation.TabOrder
import com.notdoppler.core.domain.source.remote.ApplicationPagingDataStore
import com.notdoppler.core.domain.source.remote.repository.ImagePagingRepository
import com.notdoppler.feature.home.state.TabOrderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState?>(UiState.Loading)
    val uiState = _uiState.asStateFlow()


    val tabOrderState = TabOrderState()

    private val _mapPagingData: SnapshotStateMap<TabOrder, MutableStateFlow<PagingData<FetchedImage.Hit>>?> =
        mutableStateMapOf()
    val mapPagingData: SnapshotStateMap<TabOrder, MutableStateFlow<PagingData<FetchedImage.Hit>>?> =
        _mapPagingData

    @OptIn(ExperimentalCoroutinesApi::class)
    val imagesFlow =
        tabOrderState.order
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), TabOrder.LATEST)
            .flatMapLatest { order ->
                val info = ImageRequestInfo(order = order)
                applicationPagingDataStore.getPager(
                    info = info,
                    source = imagePagingRepository.getPagingSource(info)
                )
            }
            .distinctUntilChanged()
            .cachedIn(viewModelScope)

    fun observeImagesFlow() = viewModelScope.launch(Dispatchers.IO) {
        imagesFlow.collect {
            _mapPagingData.getOrPut(tabOrderState.order.value) {
                MutableStateFlow(PagingData.empty())
            }?.value = it
        }
    }

    fun updateUiState(uiState: UiState?) {
        _uiState.value = uiState
    }


    sealed class UiState {
        data object Loading : UiState()
        data class Error(val message: String) : UiState()
    }
}