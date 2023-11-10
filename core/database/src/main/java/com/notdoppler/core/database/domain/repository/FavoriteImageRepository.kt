package com.notdoppler.core.database.domain.repository

import androidx.paging.PagingSource
import com.notdoppler.core.database.domain.model.FavoriteImage


interface FavoriteImageRepository {
    suspend fun updateById(favoriteImage: FavoriteImage)

    fun pagingSource(): PagingSource<Int, FavoriteImage>

    suspend fun deleteById(imageId: Int)

    suspend fun getCount(): Int
    suspend fun checkForFavorite(imageId: Int): Boolean
}