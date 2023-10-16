package com.notdoppler.core.network.di

import com.notdoppler.core.domain.source.remote.RetrofitPixabayService
import com.notdoppler.core.network.source.PixabayService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NetworkRepositoryModule {

    @Binds
    @Singleton
    fun provideRetrofitPixabayService(pixabayService: PixabayService): RetrofitPixabayService
}