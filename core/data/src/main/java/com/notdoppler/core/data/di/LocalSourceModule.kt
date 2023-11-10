package com.notdoppler.core.data.di

import com.notdoppler.core.data.source.local.StringResourceProviderImpl
import com.notdoppler.core.data.source.local.repository.AppPreferencesDataStoreRepositoryImpl
import com.notdoppler.core.data.source.local.repository.FavoriteImageRepositoryImpl
import com.notdoppler.core.database.domain.repository.FavoriteImageRepository
import com.notdoppler.core.domain.source.local.StringResourceProvider
import com.notdoppler.core.domain.source.local.repository.AppPreferencesDataStoreRepository
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
    fun provideAppPreferencesDataStoreRepository(appPreferencesDataStoreRepositoryImpl: AppPreferencesDataStoreRepositoryImpl): AppPreferencesDataStoreRepository
}