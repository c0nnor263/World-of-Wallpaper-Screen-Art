package com.notdoppler.feature.picturedetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.notdoppler.core.domain.model.FetchedImage
import com.notdoppler.core.domain.presentation.TabCategory
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

     fun getPagerImages(key:Int) = viewModelScope.launch(Dispatchers.IO) {
        imagesRepository.getPagerImages(category = TabCategory.RECENT, pageSize = 1, prefetchDistance = 5, key =key )
            .distinctUntilChanged()
            .cachedIn(viewModelScope).collect {
                _imagesState.value = it
            }
    }
}