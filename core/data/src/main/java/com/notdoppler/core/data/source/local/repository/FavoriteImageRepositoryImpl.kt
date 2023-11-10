package com.notdoppler.core.data.source.local.repository

import androidx.paging.PagingSource
import com.notdoppler.core.database.dao.FavoriteImageDao
import com.notdoppler.core.database.domain.model.FavoriteImage
import com.notdoppler.core.database.domain.repository.FavoriteImageRepository
import javax.inject.Inject

class FavoriteImageRepositoryImpl @Inject constructor(
    private val favoriteImageDao: FavoriteImageDao,
) : FavoriteImageRepository {
    override suspend fun updateById(favoriteImage: FavoriteImage) {
        favoriteImageDao.upsert(favoriteImage)
    }

    override fun pagingSource(): PagingSource<Int, FavoriteImage> {
        return favoriteImageDao.pagingSource()
    }

    override suspend fun getCount(): Int {
        return favoriteImageDao.getCount()
    }

    override suspend fun deleteById(imageId: Int) {
        favoriteImageDao.deleteById(imageId)
    }

    override suspend fun checkForFavorite(imageId: Int): Boolean {
        return favoriteImageDao.checkForFavorite(imageId) > 0
    }
}