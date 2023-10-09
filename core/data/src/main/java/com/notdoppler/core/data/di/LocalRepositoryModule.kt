package com.notdoppler.core.data.di

import com.notdoppler.core.data.source.local.repository.FavoriteImageRepositoryImpl
import com.notdoppler.core.database.domain.repository.FavoriteImageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LocalRepositoryModule {

    @Binds
    fun provideFavoriteImageRepository(favoriteImageRepositoryImpl: FavoriteImageRepositoryImpl): FavoriteImageRepository
}