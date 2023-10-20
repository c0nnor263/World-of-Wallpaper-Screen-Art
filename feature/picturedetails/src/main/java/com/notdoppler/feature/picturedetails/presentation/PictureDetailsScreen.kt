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
import com.notdoppler.core.domain.model.navigation.PictureDetailsNavArgs
import com.notdoppler.core.domain.model.navigation.SearchNavArgs
import com.notdoppler.core.domain.model.remote.FetchedImage
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
    viewModel: PictureDetailsViewModel = hiltViewModel(),
    navArgs: PictureDetailsNavArgs,
    onNavigateToSearch: (SearchNavArgs?) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val publisherInfoState = rememberPublisherInfoState()
    val images = viewModel.imageState.collectAsLazyPagingItems()
    var loadingDownloadDialogVisible by remember {
        mutableStateOf(false)
    }


    LaunchedEffect(Unit) {
        viewModel.setTabOrder(navArgs.tabOrder)
        viewModel.uiState.collect { state ->
            when (state) {
                PictureDetailsViewModel.UiState.StartDownload -> {
                    loadingDownloadDialogVisible = true
                }

                PictureDetailsViewModel.UiState.Downloaded -> {

                    val message = context.getString(R.string.downloaded)
                    showToast(context, message) {
                        viewModel.clearUiState()
                    }
                    loadingDownloadDialogVisible = false
                }

                PictureDetailsViewModel.UiState.SavedToFavorites -> {
                    val message = context.getString(R.string.saved_to_favorites)
                    showToast(context, message) {
                        viewModel.clearUiState()
                    }
                }

                is PictureDetailsViewModel.UiState.Share -> {
                    showShareDialog(context, state.uri) {
                        viewModel.clearUiState()
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
                    viewModel.clearUiState()
                }

                null -> {}
            }
        }
    }


    CompositionLocalProvider(LocalFavoriteIconEnabled provides viewModel.pictureDetailsState.isFavoriteEnabled) {
        val pagerState = rememberPagerState(initialPage = navArgs.selectedImageIndex) {
            images.itemCount
        }

        LaunchedEffect(pagerState.currentPage) {
            if (images.itemCount > 0) {
                val imageId = images[pagerState.currentPage]?.id
                viewModel.checkForFavorite(imageId)
            }
        }

        PictureDetailsScreenContent(
            pagerState = pagerState,
            imageHits = images,
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxSize()
                .testTag(PictureDetailsScreenTag)
        ) { type, image, bitmap ->
            viewModel.onActionClick(type, image, bitmap)
        }
    }

    LoadingDownload(visible = loadingDownloadDialogVisible)

    PublisherInfoDialog(
        state = publisherInfoState,
        onTagSearch = { tag ->
            onNavigateToSearch(
                SearchNavArgs(
                    query = tag,
                    tabOrder = navArgs.tabOrder
                )
            )
        }
    )
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