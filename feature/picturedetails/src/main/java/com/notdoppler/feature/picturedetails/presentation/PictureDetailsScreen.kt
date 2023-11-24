package com.notdoppler.feature.picturedetails.presentation

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.notdoppler.feature.picturedetails.state.LocalPictureDetailsUiState
import com.notdoppler.feature.picturedetails.state.PagerDetailState
import com.notdoppler.feature.picturedetails.state.rememberPagerDetailState
import com.notdoppler.feature.picturedetails.state.rememberPublisherInfoState

const val PictureDetailsScreenTag = "PictureDetailsScreenTag"
const val PictureDetailsScreenContentImage = "PictureDetailsScreenContentImage"

@Composable
fun PictureDetailsScreen(
    viewModel: PictureDetailsViewModel = hiltViewModel(),
    navArgs: PictureDetailsNavArgs,
    onNavigateToSearch: (SearchNavArgs?) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    var loadingDownloadDialogVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val publisherInfoState = rememberPublisherInfoState()
    val pagerDetailState = rememberPagerDetailState(
        initialPage = navArgs.selectedImageIndex,
        imagesState = viewModel.imageState,
        onGetNativeAd = viewModel::getNativeAdById,
        onCheckFavorite = viewModel::checkForFavorite
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_DESTROY -> {
                    viewModel.onActivityDestroy()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(uiState.value) {
        when (val state = uiState.value) {
            PictureDetailsViewModel.UiState.NoMoreFavorites -> {
                onNavigateBack()
            }

            PictureDetailsViewModel.UiState.ImageStateLoading,
            PictureDetailsViewModel.UiState.Actions.StartSavingPictureToDevice
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
                loadingDownloadDialogVisible = false
                viewModel.clearUiState()
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
        viewModel.setPagingData(navArgs.pagingKey, navArgs.query)
    }

    CompositionLocalProvider(
        LocalFavoriteIconEnabled provides viewModel.pictureDetailsState.isFavoriteEnabled,
        LocalPictureDetailsUiState provides uiState.value
    ) {
        PictureDetailsScreenContent(
            pagerState = pagerDetailState,
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
                .testTag(PictureDetailsScreenTag),
            onActionClick = viewModel::onActionClick
        )
    }

    LoadingBar(visible = loadingDownloadDialogVisible)

    PublisherInfoDialog(
        state = publisherInfoState,
        onTagSearch = { tag ->
            onNavigateToSearch(
                SearchNavArgs(
                    query = tag,
                    pagingKey = navArgs.pagingKey
                )
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PictureDetailsScreenContent(
    modifier: Modifier = Modifier,
    pagerState: PagerDetailState,
    onNavigateBack: () -> Unit,
    onImageStateChanged: (AsyncImagePainter.State) -> Unit,
    onActionClick: (ActionType, FetchedImage.Hit?, Bitmap?) -> Unit
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState.pagerState,
        key = pagerState::getKey
    ) { pageIndex ->
        val pageData = remember { pagerState.getPageData(pageIndex) }
        val isActiveNow by remember {
            derivedStateOf {
                pagerState.isActiveNow(pageIndex)
            }
        }

        DetailImage(
            pageData = pageData,
            isActiveNow = isActiveNow,
            onActionClick = onActionClick,
            onNavigateBack = onNavigateBack,
            onImageStateChanged = onImageStateChanged,
            modifier = Modifier
                .fillMaxSize()
                .testTag(PictureDetailsScreenContentImage)
        )
    }
}
