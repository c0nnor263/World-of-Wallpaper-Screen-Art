package com.notdoppler.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import com.notdoppler.core.domain.presentation.TabOrder
import com.notdoppler.core.domain.source.remote.ApplicationPagingDataStore
import com.notdoppler.core.domain.source.remote.repository.ImagePagingRepository
import com.notdoppler.feature.home.state.TabOrderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val imagePagingRepository: ImagePagingRepository,
    private val applicationPagingDataStore: ApplicationPagingDataStore,
) : ViewModel() {
    val tabOrderState = TabOrderState()

    @OptIn(ExperimentalCoroutinesApi::class)
    val imageState =
        tabOrderState.order
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), TabOrder.LATEST)
            .flatMapLatest { order ->
                val info = ImageRequestInfo(order = order)
                applicationPagingDataStore.getPager(
                    order.requestValue,
                    info,
                    imagePagingRepository.getPagingSource(info)
                ).flow
            }
            .distinctUntilChanged()
            .cachedIn(viewModelScope)

}