package com.doodle.core.picturedetails

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.doodle.core.database.domain.repository.FavoriteImageRepository
import com.doodle.core.domain.source.local.repository.StorageManager
import com.doodle.core.domain.source.remote.repository.RemoteImagePagingRepository
import com.doodle.core.picturedetails.domain.matchers.IntentShareMatcher
import com.doodle.core.picturedetails.domain.matchers.chooser
import com.doodle.core.ui.theme.WallpapersTheme
import com.doodle.feature.picturedetails.presentation.PictureDetailsScreenTag
import com.doodle.feature.picturedetails.presentation.common.actions.PictureDetailsScreenContentShareTag
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
    lateinit var remoteImagePagingRepository: RemoteImagePagingRepository

    @Inject
    lateinit var favoriteImageRepository: FavoriteImageRepository

    @Inject
    lateinit var storageManager: StorageManager

    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.setContent {
            WallpapersTheme {
//                val homeScreenViewModel =
//                    com.doodle.feature.home.presentation.HomeScreenViewModel(
//                        imagePagingRepository
//                    )
//                val pictureDetailsViewModel = PictureDetailsViewModel(
//                    favoriteImageRepository, storageManager
//                )
//
//                LaunchedEffect(key1 = Unit) {
//                    homeScreenViewModel.getImages(TabOrder.LATEST)
//                }
//
//                PictureDetailsScreen(
//                    homeScreenViewModel,
//                    pictureDetailsViewModel,
//                    PictureDetailsNavArgs(0, TabOrder.LATEST)
//                ) {}
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
