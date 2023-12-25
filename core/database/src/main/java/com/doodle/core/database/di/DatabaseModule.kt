package com.doodle.core.database.di

import android.content.Context
import androidx.room.Room
import com.doodle.core.database.FavoriteDatabase
import com.doodle.core.database.dao.FavoriteImageDao
import com.doodle.core.database.domain.FAVORITE_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideFavoriteDatabase(
        @ApplicationContext context: Context
    ): FavoriteDatabase {
        return Room.databaseBuilder(
            context,
            FavoriteDatabase::class.java,
            FAVORITE_DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFavoriteImageDao(
        favoriteDatabase: FavoriteDatabase
    ): FavoriteImageDao = favoriteDatabase.favoriteImageDao()
}
