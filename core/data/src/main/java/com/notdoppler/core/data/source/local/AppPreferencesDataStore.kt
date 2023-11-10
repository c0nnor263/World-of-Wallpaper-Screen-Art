package com.notdoppler.core.data.source.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject

class AppPreferencesDataStore @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) {
    companion object {
        private const val PREFERENCES_NAME = "app_preferences"
        val Context.appPreferencesDataStore by preferencesDataStore(
            name = PREFERENCES_NAME
        )
    }

    object Keys {
        val IS_AVAILABLE_FOR_REVIEW = booleanPreferencesKey("is_available_for_review")
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
}