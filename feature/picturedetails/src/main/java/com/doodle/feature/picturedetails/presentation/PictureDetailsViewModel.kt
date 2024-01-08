package com.doodle.feature.picturedetails.presentation

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
import com.doodle.core.advertising.data.NativeAdManager
import com.doodle.core.data.domain.ApplicationPagingDataStore
import com.doodle.core.database.domain.model.FavoriteImage
import com.doodle.core.database.domain.repository.FavoriteImageRepository
import com.doodle.core.domain.di.IoDispatcher
import com.doodle.core.domain.enums.ActionType
import com.doodle.core.domain.enums.PagingKey
import com.doodle.core.domain.model.remote.ImageRequestInfo
import com.doodle.core.domain.model.remote.RemoteImage
import com.doodle.core.domain.source.local.StringResourceProvider
import com.doodle.core.domain.source.local.repository.StorageManager
import com.doodle.core.domain.source.remote.repository.RemoteImagePagingRepository
import com.doodle.feature.picturedetails.R
import com.doodle.feature.picturedetails.domain.model.PublisherInfoData
import com.doodle.feature.picturedetails.state.PictureDetailsState
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val favoriteImageRepository: FavoriteImageRepository,
    private val storageManager: StorageManager,
    private val remoteImagePagingRepository: RemoteImagePagingRepository,
    private val applicationPagingDataStore: ApplicationPagingDataStore,
    private val stringResourceProvider: StringResourceProvider,
    private val nativeAdManager: NativeAdManager
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    val pictureDetailsState = PictureDetailsState()

    @OptIn(ExperimentalCoroutinesApi::class)
    val imageState = pictureDetailsState.pagingKey.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        PagingKey.LATEST
    ).flatMapLatest { order ->
        order?.let {
            val info = ImageRequestInfo(
                options = ImageRequestInfo.RemoteOption(
                    order = order
                )
            )
            getOrCreatePager(info)
        } ?: flowOf(PagingData.empty())
    }
        .distinctUntilChanged()
        .cachedIn(viewModelScope)

    fun setPagingData(key: PagingKey, query: String) {
        pictureDetailsState.updateData(key, query)
    }

    fun checkForFavorite(imageId: Int?) = viewModelScope.launch(ioDispatcher) {
        pictureDetailsState.isFavoriteEnabled = imageId?.let {
            favoriteImageRepository.checkForFavorite(it)
        } ?: false
    }

    private fun isPictureReadyForActions() =
        uiState.value !is UiState.ImageStateLoading &&
            uiState.value !is UiState.Error

    fun onActionClick(
        type: ActionType,
        image: RemoteImage.Hit?,
        bitmap: Bitmap?
    ) = viewModelScope.launch {
        if (isPictureReadyForActions()) {
            updateUiState(
                UiState.Error(stringResourceProvider.getString(R.string.image_is_not_ready))
            )
        }
        when (type) {
            ActionType.FAVORITE -> {
                updateUiState(UiState.Actions.SavedPictureToFavorites)

                val info = image?.createStorageInfo(bitmap) ?: return@launch
                storageManager.saveToGallery(info) { resultUri ->
                    val favoriteImage = FavoriteImage(
                        fileUri = resultUri.toString(),
                        imageId = image.id ?: 0
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
                    val msg = stringResourceProvider.getString(R.string.failed_to_download_image)
                    UiState.Error(msg)
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

            ActionType.SET_WALLPAPER -> {
                val info = image?.createStorageInfo(bitmap) ?: return@launch
                storageManager.saveToGallery(info) { uri ->
                    val newState = uri?.let {
                        UiState.Actions.SetWallpaper(uri = uri)
                    }
                    updateUiState(newState)
                }
            }

            ActionType.NOT_DEFINED -> {}
        }
    }

    private fun upsertFavoriteImage(image: FavoriteImage) = viewModelScope.launch(ioDispatcher) {
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

    private fun getOrCreatePager(info: ImageRequestInfo): Flow<PagingData<RemoteImage.Hit>> {
        return if (info.options.order != PagingKey.FAVORITES) {
            applicationPagingDataStore.getPager(
                key = info.options.order.remoteOptionQuery + pictureDetailsState.query,
                info = info,
                source = remoteImagePagingRepository.getPagingSource(info)
            )
        } else {
            Pager(
                config = PagingConfig(
                    pageSize = info.pageSize,
                    prefetchDistance = info.prefetchDistance
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

    fun getNativeAdById(id: Int): NativeAd? {
        return if (pictureDetailsState.pagingKey.value != PagingKey.FAVORITES) {
            nativeAdManager.getNativeAdById(id)
        } else {
            null
        }
    }

    fun dismissNativeAd(id: Int) {
        nativeAdManager.dismissAd(id)
    }

    fun destroyNativeAds() {
        nativeAdManager.onActivityDestroy()
    }

    sealed class UiState {
        data class Error(val message: String) : UiState()
        data object ImageStateLoaded : UiState()
        data object ImageStateLoading : UiState()

        data object NoMoreFavorites : UiState()

        sealed class Actions {

            @Stable
            data class SetWallpaper(val uri: Uri) : UiState()

            @Stable
            data class Share(val uri: Uri) : UiState()
            data class ShowPublisherInfo(val data: PublisherInfoData) : UiState()
            data object SavedPictureToFavorites : UiState()
            data object StartSavingPictureToDevice : UiState()
            data object FinishedSavingPictureToDevice : UiState()
        }
    }
}
