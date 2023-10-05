package com.notdoppler.core.network.di

import com.notdoppler.core.domain.source.remote.repositories.RemoteImageSource
import com.notdoppler.core.network.data.repository.RemoteImageSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NetworkRepositoryModule {
    @Binds
    fun provideImageSourceRepository(fetchedImageSourceImpl: RemoteImageSourceImpl): RemoteImageSource
}