package com.notdoppler.core.database.di

import com.notdoppler.core.database.InternalStorageManager
import com.notdoppler.core.domain.source.local.repository.StorageManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseRepositoryModule {


    @Binds
    fun provideStorageManager(internalStorageManager: InternalStorageManager): StorageManager
}