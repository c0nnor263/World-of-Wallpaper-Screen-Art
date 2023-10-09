package com.notdoppler.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import com.notdoppler.core.database.domain.model.FavoriteImage


@Dao
interface FavoriteImageDao {

    @Upsert
    suspend fun upsert(favoriteImage: FavoriteImage)

    @Delete
    suspend fun delete(favoriteImage: FavoriteImage)

}