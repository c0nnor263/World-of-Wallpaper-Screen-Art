package com.notdoppler.core.data.di

import com.notdoppler.core.data.source.remote.repository.ImagesRepositoryImpl
import com.notdoppler.core.domain.source.remote.repositories.ImagesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RemoteRepositoryModule {
    @Binds
    fun provideImagesRepository(imagesRepositoryImpl: ImagesRepositoryImpl): ImagesRepository

}