package com.doodle.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.doodle.core.database.domain.model.FavoriteImage

@Dao
interface FavoriteImageDao {

    @Upsert
    suspend fun upsert(favoriteImage: FavoriteImage)

    @Query("SELECT * FROM FavoriteImage")
    suspend fun getAll(): List<FavoriteImage>

    @Query("SELECT * FROM FavoriteImage")
    fun pagingSource(): PagingSource<Int, FavoriteImage>

    @Query("SELECT COUNT(*) FROM FavoriteImage")
    fun getCount(): Int

    @Query("SELECT COUNT(*) FROM FavoriteImage WHERE imageId LIKE :imageId")
    fun checkForFavorite(imageId: Int): Int

    @Query("DELETE FROM FavoriteImage WHERE imageId LIKE :imageId")
    suspend fun deleteById(imageId: Int)
}
