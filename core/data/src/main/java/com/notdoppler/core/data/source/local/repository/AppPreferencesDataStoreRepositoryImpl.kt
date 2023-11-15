package com.notdoppler.core.data.source.local.repository

import com.notdoppler.core.data.source.local.AppPreferencesDataStore
import com.notdoppler.core.domain.source.local.repository.AppPreferencesDataStoreRepository
import javax.inject.Inject

class AppPreferencesDataStoreRepositoryImpl @Inject constructor(
    private val appPreferencesDataStore: AppPreferencesDataStore,
) :
    AppPreferencesDataStoreRepository {
    override suspend fun setIsAvailableForReview(value: Boolean) {
        appPreferencesDataStore.setIsAvailableForReview(value)
    }

    override suspend fun getIsAvailableForReview(): Boolean {
        return appPreferencesDataStore.getIsAvailableForReview()
    }

    override suspend fun incrementAppOpenTimes() {
        appPreferencesDataStore.incrementAppOpenTimes()
    }

    override suspend fun getIsAvailableForAppOpenAd(): Boolean {
        return appPreferencesDataStore.getIsAvailableForAppOpenAd()
    }
}