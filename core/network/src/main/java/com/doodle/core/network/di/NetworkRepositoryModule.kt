package com.doodle.core.network.di

import com.doodle.core.domain.source.remote.RemoteImagePixabaySource
import com.doodle.core.domain.source.remote.repository.TagImageRepository
import com.doodle.core.network.data.repo.RemoteImagePixabaySourceImpl
import com.doodle.core.network.data.repo.TagImageRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NetworkRepositoryModule {
    @Binds
    fun provideRemoteImageSource(remoteImagePixabaySourceImpl: RemoteImagePixabaySourceImpl): RemoteImagePixabaySource

    @Binds
    fun provideTagImageRepository(tagImageRepository: TagImageRepositoryImpl): TagImageRepository
}
