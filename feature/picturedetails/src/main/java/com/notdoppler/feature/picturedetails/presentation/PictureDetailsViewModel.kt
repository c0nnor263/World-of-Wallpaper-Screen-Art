package com.notdoppler.feature.picturedetails.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notdoppler.core.database.domain.model.FavoriteImage
import com.notdoppler.core.domain.enums.ActionType
import com.notdoppler.core.domain.model.FetchedImage
import com.notdoppler.core.domain.source.local.repository.StorageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PictureDetailsViewModel @Inject constructor(
    private val favoriteImageRepository: com.notdoppler.core.database.domain.repository.FavoriteImageRepository,
    private val storageManager: StorageManager
) : ViewModel() {

    private val _uiState: MutableStateFlow<PictureDetailsUiState?> =
        MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    fun onActionClick(
        type: ActionType,
        image: FetchedImage.Hit?,
        bitmap: Bitmap?
    ) = viewModelScope.launch {
        when (type) {
            ActionType.FAVORITE -> {
                _uiState.value = PictureDetailsUiState.SavedToFavorites
                bitmap?.let {
                    val favoriteImage = FavoriteImage(bitmap = it)
                    favoriteImageRepository.upsert(favoriteImage)
                }
            }

            ActionType.DOWNLOAD -> {
                _uiState.value = PictureDetailsUiState.StartDownload
                val result = storageManager.saveToGallery(bitmap)
                _uiState.value = if (result) {
                    PictureDetailsUiState.Downloaded
                } else {
                    PictureDetailsUiState.Error("Failed to download image")
                }
            }

            ActionType.SHARE -> {
                _uiState.value = PictureDetailsUiState.Share
                // TODO: Implement share
            }
        }
    }


    sealed class PictureDetailsUiState {
        data object SavedToFavorites : PictureDetailsUiState()
        data class Error(val message: String) : PictureDetailsUiState()
        data object Share : PictureDetailsUiState()
        data object StartDownload : PictureDetailsUiState()
        data object Downloaded : PictureDetailsUiState()
    }


    override fun onCleared() {
        super.onCleared()
        Log.i("TAG", "onCleared:")
    }
}