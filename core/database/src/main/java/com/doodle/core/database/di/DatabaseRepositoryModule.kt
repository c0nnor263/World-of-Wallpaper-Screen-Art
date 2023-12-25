package com.doodle.core.database.di

import com.doodle.core.database.InternalStorageManager
import com.doodle.core.domain.source.local.repository.StorageManager
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
