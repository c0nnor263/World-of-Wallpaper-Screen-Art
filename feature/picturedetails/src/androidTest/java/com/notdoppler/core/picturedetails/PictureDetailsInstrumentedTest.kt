package com.notdoppler.core.picturedetails

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.notdoppler.core.database.domain.repository.FavoriteImageRepository
import com.notdoppler.core.domain.model.navigation.PictureDetailsNavArgs
import com.notdoppler.core.domain.presentation.TabOrder
import com.notdoppler.core.domain.source.local.repository.StorageManager
import com.notdoppler.core.domain.source.remote.repository.ImagePagingRepository
import com.notdoppler.core.picturedetails.domain.matchers.IntentShareMatcher
import com.notdoppler.core.picturedetails.domain.matchers.chooser
import com.notdoppler.core.ui.theme.WallpapersTheme
import com.notdoppler.feature.home.presentation.HomeScreenViewModel
import com.notdoppler.feature.picturedetails.presentation.PictureDetailsScreen
import com.notdoppler.feature.picturedetails.presentation.PictureDetailsScreenTag
import com.notdoppler.feature.picturedetails.presentation.PictureDetailsViewModel
import com.notdoppler.feature.picturedetails.presentation.common.actions.PictureDetailsScreenContentShareTag
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class PictureDetailsInstrumentedTest {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val intentRule = IntentsRule()

    @get:Rule(order = 3)
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var imagePagingRepository: ImagePagingRepository

    @Inject
    lateinit var favoriteImageRepository: FavoriteImageRepository

    @Inject
    lateinit var storageManager: StorageManager

    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.setContent {
            WallpapersTheme {
                val homeScreenViewModel =
                    com.notdoppler.feature.home.presentation.HomeScreenViewModel(
                        imagePagingRepository
                    )
                val pictureDetailsViewModel = PictureDetailsViewModel(
                    favoriteImageRepository, storageManager
                )

                LaunchedEffect(key1 = Unit) {
                    homeScreenViewModel.getImages(TabOrder.LATEST)
                }

                PictureDetailsScreen(
                    homeScreenViewModel,
                    pictureDetailsViewModel,
                    PictureDetailsNavArgs(0, TabOrder.LATEST)
                ) {}
            }
        }
    }

    @Test
    fun clickShareButton_showShareIntent() {
        composeTestRule.onNodeWithTag(PictureDetailsScreenTag)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(PictureDetailsScreenContentShareTag)
            .assertIsDisplayed()
            .performClick()

        intended(chooser(IntentShareMatcher()))
    }
}