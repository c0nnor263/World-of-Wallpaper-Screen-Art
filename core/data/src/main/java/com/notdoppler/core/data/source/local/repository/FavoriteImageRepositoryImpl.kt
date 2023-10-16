package com.notdoppler.core.data.source.local.repository

import android.util.Log
import com.notdoppler.core.database.dao.FavoriteImageDao
import com.notdoppler.core.database.domain.model.FavoriteImage
import com.notdoppler.core.database.domain.repository.FavoriteImageRepository
import javax.inject.Inject

class FavoriteImageRepositoryImpl @Inject constructor(
    private val favoriteImageDao: FavoriteImageDao,
) : FavoriteImageRepository {
    override suspend fun upsert(favoriteImage: FavoriteImage) {
        favoriteImageDao.upsert(favoriteImage)
    }

    override suspend fun delete(favoriteImage: FavoriteImage) {
        favoriteImageDao.delete(favoriteImage)
    }

    override suspend fun deleteById(imageId: Int) {
        favoriteImageDao.deleteById(imageId)
    }

    override suspend fun checkForFavorite(imageId: Int): Boolean {
        return favoriteImageDao.checkForFavorite(imageId).also {
            Log.i("TAG", "checkForFavorite: $imageId $it")
        } > 0
    }
}