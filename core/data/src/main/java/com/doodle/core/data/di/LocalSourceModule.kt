package com.doodle.core.data.di

import com.doodle.core.data.source.local.StringResourceProviderImpl
import com.doodle.core.data.source.local.repository.AppPreferencesDataStoreRepositoryImpl
import com.doodle.core.data.source.local.repository.FavoriteImageRepositoryImpl
import com.doodle.core.data.source.local.repository.UserPreferencesDataStoreRepositoryImpl
import com.doodle.core.database.domain.repository.FavoriteImageRepository
import com.doodle.core.domain.source.local.StringResourceProvider
import com.doodle.core.domain.source.local.repository.AppPreferencesDataStoreRepository
import com.doodle.core.domain.source.local.repository.UserPreferencesDataStoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LocalSourceModule {

    @Binds
    fun provideStringResourceProvider(stringResourceProviderImpl: StringResourceProviderImpl): StringResourceProvider

    @Binds
    fun provideFavoriteImageRepository(favoriteImageRepositoryImpl: FavoriteImageRepositoryImpl): FavoriteImageRepository

    @Binds
    fun provideAppPreferencesDataStoreRepository(
        appPreferencesDataStoreRepositoryImpl: AppPreferencesDataStoreRepositoryImpl
    ): AppPreferencesDataStoreRepository

    @Binds
    fun provideUserPreferencesDataStoreRepository(
        userPreferencesDataStoreRepositoryImpl: UserPreferencesDataStoreRepositoryImpl
    ): UserPreferencesDataStoreRepository
}
