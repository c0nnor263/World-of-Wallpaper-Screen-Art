package com.notdoppler.feature.picturedetails.presentation

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.notdoppler.core.domain.enums.ActionType
import com.notdoppler.core.domain.model.PictureDetailsNavArgs
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.ui.HomeScreenViewModel
import com.notdoppler.feature.picturedetails.R
import com.notdoppler.feature.picturedetails.presentation.common.DetailsImage
import com.notdoppler.feature.picturedetails.presentation.common.dialog.LoadingDownload
import com.notdoppler.feature.picturedetails.presentation.common.dialog.PublisherInfoDialog
import com.notdoppler.feature.picturedetails.showShareDialog
import com.notdoppler.feature.picturedetails.showToast
import com.notdoppler.feature.picturedetails.state.LocalFavoriteIconEnabled
import com.notdoppler.feature.picturedetails.state.rememberPublisherInfoState

const val PictureDetailsScreenTag = "PictureDetailsScreenTag"
const val PictureDetailsScreenContentImage = "PictureDetailsScreenContentImage"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PictureDetailsScreen(
    homeSharedViewModel: HomeScreenViewModel = hiltViewModel(),
    pictureDetailsViewModel: PictureDetailsViewModel = hiltViewModel(),
    navArgs: PictureDetailsNavArgs,
    onNavigateBack: () -> Unit,
) {


    val imageHits =
        homeSharedViewModel.tabPagingState[navArgs.tabOrder]?.collectAsLazyPagingItems()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var loadingDownloadDialogVisible by remember {
        mutableStateOf(false)
    }
    val publisherInfoState = rememberPublisherInfoState()



    LaunchedEffect(Unit) {
        pictureDetailsViewModel.uiState.collect { state ->
            when (state) {
                PictureDetailsViewModel.UiState.StartDownload -> {
                    loadingDownloadDialogVisible = true
                }

                PictureDetailsViewModel.UiState.Downloaded -> {

                    val message = context.getString(R.string.downloaded)
                    showToast(context, message) {
                        pictureDetailsViewModel.clearUiState()
                    }
                    loadingDownloadDialogVisible = false
                }

                PictureDetailsViewModel.UiState.SavedToFavorites -> {
                    val message = context.getString(R.string.saved_to_favorites)
                    showToast(context, message) {
                        pictureDetailsViewModel.clearUiState()
                    }
                }

                is PictureDetailsViewModel.UiState.Share -> {
                    showShareDialog(context, state.uri) {
                        pictureDetailsViewModel.clearUiState()
                    }
                }

                is PictureDetailsViewModel.UiState.Error -> {
                    snackbarHostState.showSnackbar(state.message)
                }

                is PictureDetailsViewModel.UiState.PublisherInfo -> {
                    publisherInfoState.apply {
                        setPublisherData(state.data)
                        show()
                    }
                    pictureDetailsViewModel.clearUiState()
                }

                null -> {}
            }
        }
    }


    CompositionLocalProvider(LocalFavoriteIconEnabled provides pictureDetailsViewModel.isFavoriteIconEnabled) {


        if (imageHits == null) {
            // TODO SHOW Error
            return@CompositionLocalProvider
        }

        val pagerState = rememberPagerState(initialPage = navArgs.selectedImageIndex) {
            imageHits.itemCount
        }

        LaunchedEffect(pagerState.currentPage) {
            val imageId = imageHits[pagerState.currentPage]?.id
            pictureDetailsViewModel.checkForFavorite(imageId)
        }

        PictureDetailsScreenContent(
            pagerState = pagerState,
            imageHits = imageHits,
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxSize()
                .testTag(PictureDetailsScreenTag)
        ) { type, image, bitmap ->
            pictureDetailsViewModel.onActionClick(type, image, bitmap)
        }
    }

    LoadingDownload(visible = loadingDownloadDialogVisible)

    PublisherInfoDialog(state = publisherInfoState, onTagSearch = {

    })
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PictureDetailsScreenContent(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    imageHits: LazyPagingItems<FetchedImage.Hit>,
    onNavigateBack: () -> Unit,
    onActionClick: (ActionType, FetchedImage.Hit?, Bitmap?) -> Unit,
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
    ) { pageIndex ->
        DetailsImage(
            imageHit = imageHits[pageIndex],
            onActionClick = onActionClick,
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxSize()
                .testTag(PictureDetailsScreenContentImage)
        )
    }
}