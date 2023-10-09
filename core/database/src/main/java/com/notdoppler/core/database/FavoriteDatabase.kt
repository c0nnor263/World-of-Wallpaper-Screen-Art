package com.notdoppler.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.notdoppler.core.database.converters.FavoriteDatabaseConverters
import com.notdoppler.core.database.dao.FavoriteImageDao
import com.notdoppler.core.database.domain.model.FavoriteImage

@Database(entities = [FavoriteImage::class], version = 1, exportSchema = false)
@TypeConverters(FavoriteDatabaseConverters::class)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteImageDao(): FavoriteImageDao
}