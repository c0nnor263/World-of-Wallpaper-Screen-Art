package com.notdoppler.feature.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.notdoppler.core.database.domain.repository.FavoriteImageRepository
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FavoritesScreenViewModel @Inject constructor(
    favoriteImageRepository: FavoriteImageRepository,
) : ViewModel() {
    private val imageRequestInfo = ImageRequestInfo()
    val favoriteImages =

        Pager(
            config = PagingConfig(
                pageSize = imageRequestInfo.pageSize,
                prefetchDistance = imageRequestInfo.prefetchDistance,
            ),
            pagingSourceFactory = {
                favoriteImageRepository.pagingSource()
            }
        ).flow.map { data ->
            data.map {
                it.mapToFetchedImageHit()
            }
        }
            .distinctUntilChanged()
            .cachedIn(viewModelScope)


}