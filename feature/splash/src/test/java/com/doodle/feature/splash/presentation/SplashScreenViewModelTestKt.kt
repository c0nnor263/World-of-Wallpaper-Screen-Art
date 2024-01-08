package com.doodle.feature.splash.presentation

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.doodle.core.advertising.data.AppOpenAdManager
import com.doodle.core.data.source.local.AppPreferencesDataStore
import com.doodle.core.data.source.local.repository.AppPreferencesDataStoreRepositoryImpl
import com.doodle.core.testing.rules.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class SplashScreenViewModelTestKt {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val testScope = TestScope(testDispatcher)

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(testDispatcher)

    private lateinit var context: Context
    private lateinit var dataStore: AppPreferencesDataStore
    private lateinit var repository: AppPreferencesDataStoreRepositoryImpl
    private lateinit var appOpenAdManager: AppOpenAdManager
    private lateinit var viewModel: SplashScreenViewModel

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        dataStore = AppPreferencesDataStore(context, testScope)
        repository =
            AppPreferencesDataStoreRepositoryImpl(dataStore)

        appOpenAdManager = AppOpenAdManager(
            context,
            testScope,
            testDispatcher,
            repository
        )

        viewModel = SplashScreenViewModel(
            testDispatcher,
            repository,
            appOpenAdManager
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun incrementAppOpenTimes_isBiggerThanZero() = runTest {
        assert(repository.getIsAvailableForAppOpenAd().not())
        viewModel.incrementAppOpenTimes()
        advanceUntilIdle()
        assertTrue(dataStore.getIsAvailableForAppOpenAdCount() == 1)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun incrementAppOpenTimesThreeTimes_isAvailableForAppOpenAd() = runTest {
        assert(repository.getIsAvailableForAppOpenAd().not())
        repeat(3) {
            viewModel.incrementAppOpenTimes()
        }
        advanceUntilIdle()
        assertTrue(repository.getIsAvailableForAppOpenAd())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun incrementAppOpenTimesTenTimes_isAvailableForAppOpenAd() = runTest {
        assert(repository.getIsAvailableForAppOpenAd().not())
        repeat(10) {
            viewModel.incrementAppOpenTimes()
        }
        advanceUntilIdle()
        assertTrue(repository.getIsAvailableForAppOpenAd())
    }
}
