package com.notdoppler.feature.picturedetails.presentation

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notdoppler.core.database.domain.model.FavoriteImage
import com.notdoppler.core.database.domain.repository.FavoriteImageRepository
import com.notdoppler.core.domain.enums.ActionType
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.source.local.repository.StorageManager
import com.notdoppler.feature.picturedetails.domain.model.PublisherInfoData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PictureDetailsViewModel @Inject constructor(
    private val favoriteImageRepository: FavoriteImageRepository,
    private val storageManager: StorageManager,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState?> =
        MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    var isFavoriteIconEnabled by mutableStateOf(false)

    fun checkForFavorite(imageId: Int?) = viewModelScope.launch(Dispatchers.IO) {
        isFavoriteIconEnabled = imageId?.let {
            favoriteImageRepository.checkForFavorite(it)
        } ?: false
    }

    fun onActionClick(
        type: ActionType,
        image: FetchedImage.Hit?,
        bitmap: Bitmap?,
    ) = viewModelScope.launch {
        when (type) {
            ActionType.FAVORITE -> {
                _uiState.value = UiState.SavedToFavorites
                bitmap?.let {
                    val favoriteImage = FavoriteImage(bitmap = it, imageId = image?.id ?: 0)
                    Log.i("TAG", "onActionClick: $favoriteImage")
                    isFavoriteIconEnabled = if (isFavoriteIconEnabled) {
                        favoriteImageRepository.deleteById(favoriteImage.imageId)
                        false
                    } else {
                        favoriteImageRepository.upsert(favoriteImage)
                        true
                    }
                }
            }

            ActionType.DOWNLOAD -> {
                _uiState.value = UiState.StartDownload

                val info = image?.createStorageInfo(bitmap) ?: return@launch
                val result = storageManager.saveToGallery(info)
                _uiState.value = if (result) {
                    UiState.Downloaded
                } else {
                    UiState.Error("Failed to download image")
                }
            }

            ActionType.SHARE -> {
                val info = image?.createStorageInfo(bitmap) ?: return@launch
                storageManager.saveToGallery(info) { uri ->
                    _uiState.value = uri?.let { UiState.Share(it) }
                }

            }

            ActionType.PUBLISHER_INFO -> {
                val data = PublisherInfoData(
                    comments = image?.comments,
                    downloads = image?.downloads,
                    imageHeight = image?.imageHeight,
                    imageWidth = image?.imageWidth,
                    largeImageURL = image?.largeImageURL,
                    likes = image?.likes,
                    pageURL = image?.pageURL,
                    previewURL = image?.previewURL,
                    tags = image?.tags,
                    type = image?.type,
                    user = image?.user,
                    userImageURL = image?.userImageURL,
                    userId = image?.user_id,
                    views = image?.views,
                )
                _uiState.value = UiState.PublisherInfo(data)
            }
        }
    }

    fun clearUiState() {
        _uiState.value = null
    }

    sealed class UiState {
        data object SavedToFavorites : UiState()
        data class Error(val message: String) : UiState()
        data class Share(val uri: Uri) : UiState()
        data class PublisherInfo(val data: PublisherInfoData) : UiState()

        data object StartDownload : UiState()
        data object Downloaded : UiState()
    }
}