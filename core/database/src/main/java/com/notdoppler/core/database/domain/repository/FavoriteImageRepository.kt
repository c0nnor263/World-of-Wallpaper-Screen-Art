package com.notdoppler.core.database.domain.repository

import com.notdoppler.core.database.domain.model.FavoriteImage


interface FavoriteImageRepository {
    suspend fun upsert(favoriteImage: FavoriteImage)
    suspend fun delete(favoriteImage: FavoriteImage)
}