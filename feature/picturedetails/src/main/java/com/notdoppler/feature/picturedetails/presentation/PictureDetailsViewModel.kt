package com.notdoppler.feature.picturedetails.presentation

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.notdoppler.core.data.domain.ApplicationPagingDataStore
import com.notdoppler.core.database.domain.model.FavoriteImage
import com.notdoppler.core.database.domain.repository.FavoriteImageRepository
import com.notdoppler.core.domain.enums.ActionType
import com.notdoppler.core.domain.enums.PagingKey
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import com.notdoppler.core.domain.source.local.StringResourceProvider
import com.notdoppler.core.domain.source.local.repository.StorageManager
import com.notdoppler.core.domain.source.remote.repository.ImagePagingRepository
import com.notdoppler.feature.picturedetails.R
import com.notdoppler.feature.picturedetails.domain.model.PublisherInfoData
import com.notdoppler.feature.picturedetails.state.PictureDetailsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PictureDetailsViewModel @Inject constructor(
    private val favoriteImageRepository: FavoriteImageRepository,
    private val storageManager: StorageManager,
    private val imagePagingRepository: ImagePagingRepository,
    private val applicationPagingDataStore: ApplicationPagingDataStore,
    private val stringResourceProvider: StringResourceProvider,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()


    val pictureDetailsState = PictureDetailsState()

    @OptIn(ExperimentalCoroutinesApi::class)
    val imageState = pictureDetailsState.tabOrder.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        PagingKey.LATEST
    ).flatMapLatest { order ->
        order?.let {
            val info = ImageRequestInfo(
                order = order,
            )
            getOrCreatePager(info)
        } ?: flowOf(PagingData.empty())
    }
        .distinctUntilChanged()
        .cachedIn(viewModelScope)

    fun setTabOrder(order: PagingKey, pagerKey: String) {
        pictureDetailsState.updateData(order, pagerKey)
    }

    fun checkForFavorite(imageId: Int?) = viewModelScope.launch(Dispatchers.IO) {
        pictureDetailsState.isFavoriteEnabled = imageId?.let {
            favoriteImageRepository.checkForFavorite(it)
        } ?: false
    }


    private fun isPictureReadyForActions() =
        uiState.value !is UiState.ImageStateLoading &&
                uiState.value !is UiState.Error

    fun onActionClick(
        type: ActionType,
        image: FetchedImage.Hit?,
        bitmap: Bitmap?,
    ) = viewModelScope.launch {
        if (isPictureReadyForActions()) {
            updateUiState(UiState.Error(stringResourceProvider.getString(R.string.image_is_not_ready)))
        }
        when (type) {
            ActionType.FAVORITE -> {
                updateUiState(UiState.Actions.SavedPictureToFavorites)

                val info = image?.createStorageInfo(bitmap) ?: return@launch
                storageManager.saveToGallery(info) { resultUri ->
                    val favoriteImage = FavoriteImage(
                        fileUri = resultUri.toString(), imageId = image.id ?: 0
                    ).copyFromFetchedHit(image)

                    upsertFavoriteImage(favoriteImage)
                }
            }

            ActionType.DOWNLOAD -> {
                updateUiState(UiState.Actions.StartSavingPictureToDevice)

                val info = image?.createStorageInfo(bitmap) ?: return@launch
                val result = storageManager.saveToGallery(info)
                val newState = if (result) {
                    UiState.Actions.FinishedSavingPictureToDevice
                } else {
                    UiState.Error(stringResourceProvider.getString(R.string.failed_to_download_image))
                }
                updateUiState(newState)
            }

            ActionType.SHARE -> {
                val info = image?.createStorageInfo(bitmap) ?: return@launch
                storageManager.saveToGallery(info) { uri ->
                    updateUiState(uri?.let { UiState.Actions.Share(it) })
                }

            }

            ActionType.PUBLISHER_INFO -> {
                val data = PublisherInfoData().createFromImage(image)
                updateUiState(UiState.Actions.ShowPublisherInfo(data))
            }
        }
    }

    private fun upsertFavoriteImage(image: FavoriteImage) = viewModelScope.launch(Dispatchers.IO) {
        if (pictureDetailsState.isFavoriteEnabled) {
            favoriteImageRepository.deleteById(image.imageId)
            if (favoriteImageRepository.getCount() == 0) {
                updateUiState(UiState.NoMoreFavorites)
            }
        } else {
            favoriteImageRepository.updateById(image)
        }
        pictureDetailsState.isFavoriteEnabled = !pictureDetailsState.isFavoriteEnabled
    }


    fun updateUiState(state: UiState?) {
        _uiState.value = state
    }

    fun clearUiState() {
        _uiState.value = null
    }


    private fun getOrCreatePager(info: ImageRequestInfo): Flow<PagingData<FetchedImage.Hit>> {
        return if (info.order != PagingKey.FAVORITES) {
            applicationPagingDataStore.getPager(
                key = info.order.requestValue + pictureDetailsState.pagerKey,
                info = info,
                source = imagePagingRepository.getPagingSource(info)
            )
        } else {
            Pager(
                config = PagingConfig(
                    pageSize = info.pageSize,
                    prefetchDistance = info.prefetchDistance,
                ),
                pagingSourceFactory = {
                    favoriteImageRepository.pagingSource()
                }
            ).flow.map { data ->
                data.map {
                    it.mapToFetchedImageHit()
                }
            }
        }
    }

    sealed class UiState {
        data class Error(val message: String) : UiState()
        data object ImageStateLoaded : UiState()
        data object ImageStateLoading : UiState()

        data object NoMoreFavorites : UiState()

        sealed class Actions {
            @Stable
            data class Share(val uri: Uri) : UiState()
            data class ShowPublisherInfo(val data: PublisherInfoData) : UiState()
            data object SavedPictureToFavorites : UiState()
            data object StartSavingPictureToDevice : UiState()
            data object FinishedSavingPictureToDevice : UiState()
        }
    }
}