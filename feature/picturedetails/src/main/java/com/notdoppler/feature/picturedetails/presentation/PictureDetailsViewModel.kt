package com.notdoppler.feature.picturedetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.notdoppler.core.domain.model.FetchedImage
import com.notdoppler.core.domain.model.ImageRequestInfo
import com.notdoppler.core.domain.presentation.TabOrder
import com.notdoppler.core.domain.source.remote.repositories.ImagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PictureDetailsViewModel @Inject constructor(
    private val imagesRepository: ImagesRepository
) : ViewModel() {

    private val _imagesState: MutableStateFlow<PagingData<FetchedImage.Hit>> =
        MutableStateFlow(value = PagingData.empty())
    val imagesState: StateFlow<PagingData<FetchedImage.Hit>> = _imagesState

    fun getPagerImages(pageKey: Int) = viewModelScope.launch(Dispatchers.IO) {
        imagesRepository.getImages(
            ImageRequestInfo(
                order = TabOrder.LATEST,
                pageSize = 3,
                prefetchDistance = 5,

                // For correct pagination, we need to increment the pageKey by 1
                pageKey = pageKey.plus(1)
            )
        )
            .distinctUntilChanged()
            .cachedIn(viewModelScope).collect {
                _imagesState.value = it
            }
    }
}