package com.doodle.feature.home.presentation

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.doodle.core.billing.data.BillingDataSource
import com.doodle.core.billing.domain.enums.BillingProductType
import com.doodle.core.data.domain.ApplicationPagingDataStore
import com.doodle.core.data.review.ApplicationReviewManager
import com.doodle.core.domain.di.IoDispatcher
import com.doodle.core.domain.enums.PagingKey
import com.doodle.core.domain.model.navigation.PictureDetailsNavArgs
import com.doodle.core.domain.model.remote.ImageRequestInfo
import com.doodle.core.domain.model.remote.RemoteImage
import com.doodle.core.domain.model.remote.TagData
import com.doodle.core.domain.source.local.StringResourceProvider
import com.doodle.core.domain.source.remote.repository.RemoteImagePagingRepository
import com.doodle.core.domain.source.remote.repository.TagImageRepository
import com.doodle.feature.home.state.HomePagingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineDispatcher
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
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val remoteImagePagingRepository: RemoteImagePagingRepository,
    private val applicationPagingDataStore: ApplicationPagingDataStore,
    private val tagImageRepository: TagImageRepository,
    private val applicationReviewManager: ApplicationReviewManager,
    private val billingDataSource: BillingDataSource,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState?>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    val homePagingState = HomePagingState(
        onUpdateTagList = {
            getDataForTagList(it)
        }
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingImageFlow = homePagingState.keyState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        PagingKey.LATEST
    )
        .flatMapLatest { order ->
            val editorsChoice = order == PagingKey.EDITORS_CHOICE
            val info = ImageRequestInfo(
                options = ImageRequestInfo.RemoteOption(
                    if (editorsChoice) PagingKey.LATEST else order,
                    isPremium = editorsChoice
                )
            )
            applicationPagingDataStore.getPager(
                key = order.remoteOptionQuery,
                info = info,
                source = remoteImagePagingRepository.getPagingSource(info)
            )
        }
        .distinctUntilChanged()
        .cachedIn(viewModelScope)

    fun updateUiState(uiState: UiState?) {
        _uiState.value = uiState
    }

    fun cacheDataForPagingKey(newValue: PagingData<RemoteImage.Hit>?) =
        viewModelScope.launch {
            homePagingState.cacheDataForCurrentKey(newValue)
        }

    private fun getDataForTagList(list: ImmutableList<TagData>) =
        viewModelScope.launch(ioDispatcher) {
            updateUiState(UiState.Loading)
            val asyncList = mutableListOf<Deferred<Unit>>()
            list.forEach { tag ->
                asyncList.add(
                    async {
                        tag.image.value = tagImageRepository.getByTitle(tag.title)
                    }
                )
            }
            asyncList.awaitAll()
            updateUiState(null)
        }

    fun requestApplicationReview(activity: ComponentActivity) = viewModelScope.launch {
        applicationReviewManager.requestInfo(activity)
    }

    fun requestBillingRemoveAds(
        activity: ComponentActivity
    ) = viewModelScope.launch {
        billingDataSource.purchaseProduct(
            BillingProductType.REMOVE_ADS,
            activity = activity,
            onError = {
                val msg =
                    stringResourceProvider.getString(
                        com.doodle.core.ui.R.string.something_went_wrong
                    )
                updateUiState(UiState.Error(msg))
            }
        )
    }

    sealed class UiState {
        data class Premium(val args: PictureDetailsNavArgs) : UiState()
        data object Loading : UiState()
        data class Error(val message: String) : UiState()
    }
}
