package com.doodle.core.data.source.local.repository

import com.doodle.core.data.source.local.UserPreferencesDataStore
import com.doodle.core.domain.enums.RemoveAdsStatus
import com.doodle.core.domain.source.local.repository.UserPreferencesDataStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataStoreRepositoryImpl @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : UserPreferencesDataStoreRepository {
    override suspend fun setRemoveAds(value: RemoveAdsStatus) {
        userPreferencesDataStore.setRemoveAds(value)
    }

    override suspend fun getRemoveAdsStatus(): RemoveAdsStatus {
        return userPreferencesDataStore.getRemoveAdsStatus()
    }

    override fun getRemoveAdsStatusFlow(): Flow<RemoveAdsStatus> {
        return userPreferencesDataStore.getRemoveAdsStatusFlow()
    }
}
