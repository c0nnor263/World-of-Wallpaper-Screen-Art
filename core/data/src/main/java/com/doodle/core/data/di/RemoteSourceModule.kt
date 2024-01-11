package com.doodle.core.data.di

import com.doodle.core.data.domain.ApplicationPagingDataStore
import com.doodle.core.data.source.remote.ApplicationPagingDataStoreImpl
import com.doodle.core.data.source.remote.repository.RemoteImagePagingRepositoryImpl
import com.doodle.core.data.source.remote.repository.SearchImagePagingRepositoryImpl
import com.doodle.core.domain.source.remote.repository.RemoteImagePagingRepository
import com.doodle.core.domain.source.remote.repository.SearchImagePagingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RemoteSourceModule {
    @Binds
    fun provideImagePagingRepository(
        remoteImagePagingRepositoryImpl: RemoteImagePagingRepositoryImpl
    ): RemoteImagePagingRepository

    @Binds
    fun provideSearchImagePagingRepository(
        searchImagePagingRepositoryImpl: SearchImagePagingRepositoryImpl
    ): SearchImagePagingRepository

    @Binds
    fun provideApplicationPagingDataStore(
        applicationPagingDataStoreImpl: ApplicationPagingDataStoreImpl
    ): ApplicationPagingDataStore
}
