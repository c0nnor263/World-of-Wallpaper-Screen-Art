package com.notdoppler.core.database.domain.repository

import com.notdoppler.core.database.domain.model.FavoriteImage


interface FavoriteImageRepository {
    suspend fun upsert(favoriteImage: FavoriteImage)
    suspend fun delete(favoriteImage: FavoriteImage)

    suspend fun deleteById(imageId: Int)
    suspend fun checkForFavorite(imageId: Int): Boolean
}