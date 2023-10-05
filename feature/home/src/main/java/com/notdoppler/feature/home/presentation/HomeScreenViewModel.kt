package com.notdoppler.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.notdoppler.core.domain.presentation.TabCategory
import com.notdoppler.core.domain.domain.model.FetchedImage
import com.notdoppler.core.domain.source.remote.repositories.ImagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val imagesRepository: ImagesRepository
) : ViewModel() {
    private val _imagesState: MutableStateFlow<PagingData<FetchedImage.Hit>> =
        MutableStateFlow(value = PagingData.empty())
    val imagesState: StateFlow<PagingData<FetchedImage.Hit>> = _imagesState.asStateFlow()

    init {
        getImages()
    }

    private fun getImages() = viewModelScope.launch(Dispatchers.IO) {
        imagesRepository.getImages(category = TabCategory.RECENT)
            .distinctUntilChanged()
            .cachedIn(viewModelScope).collect {
                _imagesState.value = it
            }
    }

}