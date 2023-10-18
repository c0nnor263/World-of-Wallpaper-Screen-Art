package com.notdoppler.core.data.di

import com.notdoppler.core.data.source.remote.ApplicationPagingDataStoreImpl
import com.notdoppler.core.data.source.remote.RemoteImageSourceImpl
import com.notdoppler.core.data.source.remote.repository.ImagePagingRepositoryImpl
import com.notdoppler.core.data.source.remote.repository.SearchImagePagingRepositoryImpl
import com.notdoppler.core.domain.source.remote.ApplicationPagingDataStore
import com.notdoppler.core.domain.source.remote.RemoteImageSource
import com.notdoppler.core.domain.source.remote.repository.ImagePagingRepository
import com.notdoppler.core.domain.source.remote.repository.SearchImagePagingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RemoteRepositoryModule {
    @Binds
    fun provideImagePagingRepository(imagePagingRepositoryImpl: ImagePagingRepositoryImpl): ImagePagingRepository

    @Binds
    fun provideRemoteImageSource(remoteImageSourceImpl: RemoteImageSourceImpl): RemoteImageSource

    @Binds
    fun provideSearchImagePagingRepository(searchImagePagingRepositoryImpl: SearchImagePagingRepositoryImpl): SearchImagePagingRepository

    @Binds
    @Singleton
    fun provideApplicationPagingDataStore(applicationPagingDataStoreImpl: ApplicationPagingDataStoreImpl): ApplicationPagingDataStore
}