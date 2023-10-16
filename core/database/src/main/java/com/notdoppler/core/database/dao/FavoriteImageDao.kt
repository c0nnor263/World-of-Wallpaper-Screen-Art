package com.notdoppler.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.notdoppler.core.database.domain.model.FavoriteImage


@Dao
interface FavoriteImageDao {

    @Upsert
    suspend fun upsert(favoriteImage: FavoriteImage)

    @Delete
    suspend fun delete(favoriteImage: FavoriteImage)

    @Query("SELECT COUNT(*) FROM FavoriteImage WHERE imageId LIKE :imageId")
    fun checkForFavorite(imageId: Int): Int

    @Query("DELETE FROM FavoriteImage WHERE imageId LIKE :imageId")
    suspend fun deleteById(imageId: Int)
}