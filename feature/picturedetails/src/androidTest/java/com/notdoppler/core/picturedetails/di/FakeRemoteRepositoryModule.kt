package com.notdoppler.core.picturedetails.di

import com.notdoppler.core.data.di.RemoteRepositoryModule
import com.notdoppler.core.domain.source.remote.RemoteImageSource
import com.notdoppler.core.domain.source.remote.repository.ImagePagingRepository
import com.notdoppler.core.picturedetails.repository.FakeImagePagingRepositoryImpl
import com.notdoppler.core.picturedetails.repository.FakeRemoteImageSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RemoteRepositoryModule::class]
)
interface RemoteRepositoryModule {
    @Binds
    fun provideImagesRepository(imagesRepositoryImpl: FakeImagePagingRepositoryImpl): ImagePagingRepository

    @Binds
    fun provideImageSourceRepository(fetchedImageSourceImpl: FakeRemoteImageSourceImpl): RemoteImageSource
}