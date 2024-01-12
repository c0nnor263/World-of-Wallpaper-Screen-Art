package com.doodle.feature.favorites.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.doodle.core.database.domain.repository.FavoriteImageRepository
import com.doodle.core.domain.model.remote.ImageRequestInfo
import com.doodle.core.domain.source.local.repository.StorageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesScreenViewModel @Inject constructor(
    private val favoriteImageRepository: FavoriteImageRepository,
    private val storageManager: StorageManager
) : ViewModel() {
    private val imageRequestInfo = ImageRequestInfo()
    val favoriteImages =

        Pager(
            config = PagingConfig(
                pageSize = imageRequestInfo.pageSize,
                prefetchDistance = imageRequestInfo.prefetchDistance
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


    fun checkIfFileExists(id: Int?, uri: Uri): Boolean {
        return storageManager.isFileExists(uri).also { exists ->
            if (!exists) {
                id?.let {
                    viewModelScope.launch {
                        favoriteImageRepository.deleteById(it)
                    }
                }
            }
        }
    }
}
