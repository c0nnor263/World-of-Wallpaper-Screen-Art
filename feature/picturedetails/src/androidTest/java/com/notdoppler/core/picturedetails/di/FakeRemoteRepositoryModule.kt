package com.notdoppler.core.picturedetails.di

import com.notdoppler.core.data.di.RemoteRepositoryModule
import com.notdoppler.core.domain.source.remote.repository.ImagesRepository
import com.notdoppler.core.domain.source.remote.repository.RemoteImageSource
import com.notdoppler.core.picturedetails.repository.FakeImagesRepositoryImpl
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
    fun provideImagesRepository(imagesRepositoryImpl: FakeImagesRepositoryImpl): ImagesRepository

    @Binds
    fun provideImageSourceRepository(fetchedImageSourceImpl: FakeRemoteImageSourceImpl): RemoteImageSource
}