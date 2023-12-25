package com.doodle.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.doodle.core.data.domain.ApplicationPagingDataStore
import com.doodle.core.domain.enums.PagingKey
import com.doodle.core.domain.model.navigation.SearchNavArgs
import com.doodle.core.domain.model.remote.ImageRequestInfo
import com.doodle.core.domain.source.remote.repository.SearchImagePagingRepository
import com.doodle.feature.search.state.SearchQueryState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val searchImagePagingRepository: SearchImagePagingRepository,
    private val applicationPagingDataStore: ApplicationPagingDataStore
) : ViewModel() {
    val searchQueryState = SearchQueryState()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val imageState =
        searchQueryState.query
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")
            .debounce(500.milliseconds)
            .onEach { searchQueryState.isSearching = true }
            .flatMapLatest { query ->
                val info = ImageRequestInfo(
                    options = ImageRequestInfo.RemoteOption(
                        query = query,
                        order = searchQueryState.pagingKey
                    )
                )
                applicationPagingDataStore.getPager(
                    key = PagingKey.SEARCH.requestValue + query,
                    info = info,
                    source = searchImagePagingRepository.getPagingSource(info)
                )
            }
            .onEach { searchQueryState.isSearching = false }
            .cachedIn(viewModelScope)

    fun setSearchState(navArgs: SearchNavArgs) {
        if (searchQueryState.query.value == navArgs.query || navArgs.query.isBlank()) return
        searchQueryState.updateQuery(navArgs.query)
        searchQueryState.updateTabOrder(navArgs.pagingKey)
    }
}
