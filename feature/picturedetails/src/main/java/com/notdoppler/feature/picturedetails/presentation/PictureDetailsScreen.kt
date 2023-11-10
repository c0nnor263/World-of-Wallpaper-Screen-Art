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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImagePainter
import com.notdoppler.core.domain.enums.ActionType
import com.notdoppler.core.domain.model.navigation.PictureDetailsNavArgs
import com.notdoppler.core.domain.model.navigation.SearchNavArgs
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.ui.LoadingBar
import com.notdoppler.feature.picturedetails.R
import com.notdoppler.feature.picturedetails.presentation.common.DetailImage
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
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(
        initialPage = navArgs.selectedImageIndex
    ) { images.itemCount }

    var loadingDownloadDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.value) {
        when (val state = uiState.value) {

            PictureDetailsViewModel.UiState.NoMoreFavorites -> {
                onNavigateBack()
            }

            PictureDetailsViewModel.UiState.ImageStateLoading,
            PictureDetailsViewModel.UiState.Actions.StartSavingPictureToDevice,
            -> {
                loadingDownloadDialogVisible = true
            }

            is PictureDetailsViewModel.UiState.ImageStateLoaded -> {
                loadingDownloadDialogVisible = false
            }

            is PictureDetailsViewModel.UiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.clearUiState()
            }

            PictureDetailsViewModel.UiState.Actions.FinishedSavingPictureToDevice -> {

                val message = context.getString(R.string.downloaded)
                showToast(context, message)
                viewModel.clearUiState()
                loadingDownloadDialogVisible = false
            }

            PictureDetailsViewModel.UiState.Actions.SavedPictureToFavorites -> {
                val message = context.getString(R.string.saved_to_favorites)
                showToast(context, message)
                viewModel.clearUiState()
            }

            is PictureDetailsViewModel.UiState.Actions.Share -> {
                showShareDialog(context, state.uri) {
                    viewModel.clearUiState()
                }
            }

            is PictureDetailsViewModel.UiState.Actions.ShowPublisherInfo -> {
                publisherInfoState.apply {
                    setPublisherData(state.data)
                    show()
                }
                viewModel.clearUiState()
            }

            null -> {}
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setTabOrder(navArgs.pagingKey, navArgs.additionalKey)
    }

    LaunchedEffect(pagerState.currentPage, Unit, images.itemCount) {
        if (images.itemCount > 0) {
            val imageId = images[pagerState.currentPage]?.id
            viewModel.checkForFavorite(imageId)
        }
    }


    CompositionLocalProvider(LocalFavoriteIconEnabled provides viewModel.pictureDetailsState.isFavoriteEnabled) {
        PictureDetailsScreenContent(
            pagerState = pagerState,
            imageHits = images,
            onNavigateBack = onNavigateBack,
            onImageStateChanged = { state ->
                when (state) {
                    is AsyncImagePainter.State.Success -> {
                        val newState = PictureDetailsViewModel.UiState.ImageStateLoaded
                        viewModel.updateUiState(newState)
                    }

                    is AsyncImagePainter.State.Loading -> {
                        viewModel.updateUiState(PictureDetailsViewModel.UiState.ImageStateLoading)
                    }

                    is AsyncImagePainter.State.Error -> {
                        val message = state.result.throwable.localizedMessage
                            ?: context.getString(R.string.error)
                        viewModel.updateUiState(PictureDetailsViewModel.UiState.Error(message))
                    }

                    is AsyncImagePainter.State.Empty -> {}

                    else -> {}
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .testTag(PictureDetailsScreenTag)
        ) { type, image, bitmap ->
            viewModel.onActionClick(type, image, bitmap)
        }
    }
    LoadingBar(visible = loadingDownloadDialogVisible)

    PublisherInfoDialog(state = publisherInfoState, onTagSearch = { tag ->
        onNavigateToSearch(
            SearchNavArgs(
                query = tag, pagingKey = navArgs.pagingKey
            )
        )
    })
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PictureDetailsScreenContent(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    imageHits: LazyPagingItems<FetchedImage.Hit>,
    onNavigateBack: () -> Unit,
    onImageStateChanged: (AsyncImagePainter.State) -> Unit,
    onActionClick: (ActionType, FetchedImage.Hit?, Bitmap?) -> Unit,
) {
    val thresholdIndex = remember {
        derivedStateOf {
            pagerState.settledPage.coerceAtMost(pagerState.currentPage + 1)
        }
    }
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        key = { imageHits[it]?.id ?: 0 },
    ) { pageIndex ->
        DetailImage(
            image = imageHits[pageIndex],
            isActive = thresholdIndex.value == pageIndex,
            onActionClick = onActionClick,
            onNavigateBack = onNavigateBack,
            onImageStateChanged = onImageStateChanged,
            modifier = Modifier
                .fillMaxSize()
                .testTag(PictureDetailsScreenContentImage)
        )
    }
}