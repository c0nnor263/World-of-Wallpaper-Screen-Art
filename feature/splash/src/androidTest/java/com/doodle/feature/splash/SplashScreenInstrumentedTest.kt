package com.doodle.feature.splash

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.doodle.core.advertising.data.AppOpenAdManager
import com.doodle.core.advertising.domain.enums.AdStatus
import com.doodle.core.domain.di.IoDispatcher
import com.doodle.core.domain.source.local.repository.AppPreferencesDataStoreRepository
import com.doodle.feature.splash.presentation.SplashScreen
import com.doodle.feature.splash.presentation.SplashScreenTag
import com.doodle.feature.splash.presentation.SplashScreenViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SplashScreenInstrumentedTest {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createComposeRule()

    @IoDispatcher
    @Inject
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var appPreferencesDataStoreRepository: AppPreferencesDataStoreRepository

    @Inject
    lateinit var appOpenAdManager: AppOpenAdManager

    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.setContent {
            val viewModel = SplashScreenViewModel(
                ioDispatcher = ioDispatcher,
                appPreferencesDataStoreRepository = appPreferencesDataStoreRepository,
                appOpenAdManager = appOpenAdManager
            )
            SplashScreen(viewModel = viewModel) {
            }
        }
    }

    @Test
    fun testSplashScreenVisibility() = runBlocking {
        composeTestRule.onNodeWithContentDescription(SplashScreenTag)
            .assertExists()

        delay(5000L)

        assertNotEquals(appOpenAdManager.adStatus.value, AdStatus.LOADING)
        composeTestRule.onNodeWithContentDescription(SplashScreenTag)
            .assertDoesNotExist()
    }
}
