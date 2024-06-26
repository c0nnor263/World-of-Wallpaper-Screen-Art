package com.doodle.core.data.source.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.doodle.core.domain.di.IoScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

const val IS_AVAILABLE_FOR_APP_OPEN_AD_THRESHOLD = 2

@Singleton
class AppPreferencesDataStore @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    @IoScope private val ioScope: CoroutineScope
) {
    companion object {
        private const val PREFERENCES_NAME = "app_preferences"
    }

    private val Context.appPreferencesDataStore by preferencesDataStore(
        name = PREFERENCES_NAME,
        scope = ioScope
    )

    object Keys {
        val IS_AVAILABLE_FOR_REVIEW = booleanPreferencesKey("is_available_for_review")
        val IS_AVAILABLE_FOR_APP_OPEN_AD = intPreferencesKey("is_available_for_app_open_ad")
    }

    private val dataStore = applicationContext.appPreferencesDataStore
    private val dataFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    suspend fun setIsAvailableForReview(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_AVAILABLE_FOR_REVIEW] = value
        }
    }

    suspend fun getIsAvailableForReview(): Boolean {
        return dataFlow.first()[Keys.IS_AVAILABLE_FOR_REVIEW] ?: true
    }

    suspend fun incrementAppOpenTimes() {
        dataStore.edit { preferences ->
            val current = preferences[Keys.IS_AVAILABLE_FOR_APP_OPEN_AD] ?: 0
            preferences[Keys.IS_AVAILABLE_FOR_APP_OPEN_AD] = current + 1
        }
    }

    suspend fun getIsAvailableForAppOpenAd(): Boolean {
        val current = getIsAvailableForAppOpenAdCount()
        return current > IS_AVAILABLE_FOR_APP_OPEN_AD_THRESHOLD
    }

    suspend fun getIsAvailableForAppOpenAdCount(): Int {
        return dataFlow.first()[Keys.IS_AVAILABLE_FOR_APP_OPEN_AD] ?: 0
    }
}
