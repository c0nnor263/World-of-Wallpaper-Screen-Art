package com.notdoppler.core.data.source.local.repository

import com.notdoppler.core.database.dao.FavoriteImageDao
import com.notdoppler.core.database.domain.model.FavoriteImage
import com.notdoppler.core.database.domain.repository.FavoriteImageRepository
import javax.inject.Inject

class FavoriteImageRepositoryImpl @Inject constructor(
    private val favoriteImageDao: FavoriteImageDao
): FavoriteImageRepository {
    override suspend fun upsert(favoriteImage: FavoriteImage) {
        favoriteImageDao.upsert(favoriteImage)
    }

    override suspend fun delete(favoriteImage: FavoriteImage) {
        favoriteImageDao.delete(favoriteImage)
    }
}