package com.doodle.core.data.source.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.doodle.core.domain.di.IoScope
import com.doodle.core.domain.enums.RemoveAdsStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    @IoScope private val ioScope: CoroutineScope
) {
    companion object {
        private const val PREFERENCES_NAME = "user_preferences"
    }

    private val Context.userPreferencesDataStore by preferencesDataStore(
        name = PREFERENCES_NAME,
        scope = ioScope
    )

    object Keys {
        val remove_ads_bought = stringPreferencesKey("remove_ads_bought")
    }

    private val dataStore = applicationContext.userPreferencesDataStore
    private val dataFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    suspend fun setRemoveAds(value: RemoveAdsStatus) {
        dataStore.edit { preferences ->
            preferences[Keys.remove_ads_bought] = value.name
        }
    }

    suspend fun getRemoveAdsStatus(): RemoveAdsStatus {
        val value = dataFlow.first()[Keys.remove_ads_bought] ?: RemoveAdsStatus.NOT_PURCHASED.name
        return RemoveAdsStatus.valueOf(value)
    }

    fun getRemoveAdsStatusFlow(): Flow<RemoveAdsStatus> {
        return dataFlow.map { preferences ->
            val value = preferences[Keys.remove_ads_bought] ?: RemoveAdsStatus.NOT_PURCHASED.name
            RemoveAdsStatus.valueOf(value)
        }
    }

}