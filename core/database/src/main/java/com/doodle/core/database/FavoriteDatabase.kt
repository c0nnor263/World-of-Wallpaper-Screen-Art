package com.doodle.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.doodle.core.database.converters.FavoriteDatabaseConverters
import com.doodle.core.database.dao.FavoriteImageDao
import com.doodle.core.database.domain.model.FavoriteImage

@Database(entities = [FavoriteImage::class], version = 1, exportSchema = false)
@TypeConverters(FavoriteDatabaseConverters::class)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteImageDao(): FavoriteImageDao
}
