package com.notdoppler.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.notdoppler.core.data.domain.ApplicationPagingDataStore
import com.notdoppler.core.domain.enums.PagingKey
import com.notdoppler.core.domain.model.navigation.SearchNavArgs
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import com.notdoppler.core.domain.source.remote.repository.SearchImagePagingRepository
import com.notdoppler.feature.search.state.SearchQueryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val searchImagePagingRepository: SearchImagePagingRepository,
    private val applicationPagingDataStore: ApplicationPagingDataStore,
) : ViewModel() {
    val searchQueryState = SearchQueryState()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val imageState =
        searchQueryState.query
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")
            .debounce(500.milliseconds)
            .onEach { searchQueryState.isSearching = true }
            .flatMapLatest { query ->
                val info = ImageRequestInfo(query = query, order = searchQueryState.pagingKey)
                applicationPagingDataStore.getPager(
                    key = PagingKey.SEARCH.requestValue + query,
                    info = info,
                    source = searchImagePagingRepository.getPagingSource(info),
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