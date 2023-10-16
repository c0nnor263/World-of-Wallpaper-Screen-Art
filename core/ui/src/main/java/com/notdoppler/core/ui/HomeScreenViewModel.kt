package com.notdoppler.core.ui

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import com.notdoppler.core.domain.presentation.TabOrder
import com.notdoppler.core.domain.source.remote.repository.ImagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val imagesRepository: ImagesRepository
) : ViewModel() {
    private val _tabPagingState: SnapshotStateMap<TabOrder, MutableStateFlow<PagingData<FetchedImage.Hit>>?> =
        mutableStateMapOf()
    val tabPagingState: SnapshotStateMap<TabOrder, MutableStateFlow<PagingData<FetchedImage.Hit>>?> =
        _tabPagingState

    fun getImages(tabOrder: TabOrder = TabOrder.LATEST) = viewModelScope.launch(Dispatchers.IO) {

        if (tabPagingState.containsKey(tabOrder).not()) {
            val info = ImageRequestInfo(order = tabOrder)
            val pagingFlow = Pager(
                config = PagingConfig(
                    pageSize = info.pageSize,
                    prefetchDistance = info.prefetchDistance
                ),
                pagingSourceFactory = {
                    imagesRepository.getImagePagingSource(info)
                }
            ).flow

            pagingFlow
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _tabPagingState
                        .getOrPut(tabOrder) {
                            MutableStateFlow(PagingData.empty())
                        }?.value = it
                }
        }
    }
}