package com.notdoppler.feature.picturedetails.presentation

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.notdoppler.core.domain.enums.ActionType
import com.notdoppler.core.domain.model.FetchedImage
import com.notdoppler.core.domain.model.PictureDetailsNavArgs
import com.notdoppler.core.ui.HomeScreenViewModel
import com.notdoppler.core.ui.R
import com.notdoppler.feature.picturedetails.domain.model.AnchoredDraggableInfo
import com.notdoppler.feature.picturedetails.presentation.common.AnchoredDraggableArea
import com.notdoppler.feature.picturedetails.presentation.common.LoadingDownload
import kotlin.math.roundToInt


@Composable
fun PictureDetailsScreen(
    homeSharedViewModel: HomeScreenViewModel = hiltViewModel(),
    pictureDetailsViewModel: PictureDetailsViewModel = hiltViewModel(),
    navArgs: PictureDetailsNavArgs,
    onNavigateBack: () -> Unit
) {

    val context = LocalContext.current
    val imageHits =
        homeSharedViewModel.tabPagingState[navArgs.tabOrder]?.collectAsLazyPagingItems() ?: return

    var loadingDownloadVisible by remember {
        mutableStateOf(false)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        pictureDetailsViewModel.uiState.collect { state ->
            when (state) {
                PictureDetailsViewModel.PictureDetailsUiState.StartDownload -> {
                    loadingDownloadVisible = true
                }

                PictureDetailsViewModel.PictureDetailsUiState.Downloaded -> {
                    loadingDownloadVisible = false
                    Toast.makeText(
                        context,
                        "Downloaded",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                PictureDetailsViewModel.PictureDetailsUiState.SavedToFavorites -> {
                    Toast.makeText(
                        context,
                        "Saved to favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                PictureDetailsViewModel.PictureDetailsUiState.Share -> {
                    // TODO share
                }

                is PictureDetailsViewModel.PictureDetailsUiState.Error -> {
                    snackbarHostState.showSnackbar(state.message)
                }

                null -> {}
            }
        }
    }

    AnchoredDraggableArea(
        modifier = Modifier.fillMaxSize(),
        onTopEnd = onNavigateBack,
    ) { draggableInfo ->
        PictureDetailsScreenContent(
            navArgs.selectedImageIndex, imageHits, draggableInfo
        ) { type, image, bitmap ->
            pictureDetailsViewModel.onActionClick(type, image, bitmap)
        }
    }


    LoadingDownload(visible = loadingDownloadVisible)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PictureDetailsScreenContent(
    selectedImageIndex: Int,
    imageHits: LazyPagingItems<FetchedImage.Hit>,
    draggableInfo: AnchoredDraggableInfo,
    onActionClick: (ActionType, FetchedImage.Hit?, Bitmap?) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = selectedImageIndex
    ) { imageHits.itemCount }

    var currentImage by remember {
        mutableStateOf(imageHits[selectedImageIndex])
    }

    var currentBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState
        ) { pageIndex ->
            currentImage = imageHits[pageIndex]

            AsyncImage(ImageRequest.Builder(LocalContext.current).data(currentImage?.largeImageURL)
                .listener { _, result ->
                    currentBitmap = (result.drawable as BitmapDrawable).bitmap
                }
                .allowHardware(false)
                .build(),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1f - draggableInfo.progress)
                    .offset {
                        IntOffset(
                            x = 0,
                            y = draggableInfo.state
                                .requireOffset()
                                .roundToInt()
                        )
                    }
                    .anchoredDraggable(draggableInfo.state, Orientation.Vertical))
        }

        ActionRow(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(
                bottom = 8.dp
            ), visible = draggableInfo.progress < 0.035F, onActionClick = {
            onActionClick(it, currentImage, currentBitmap)
        })
    }
}

@Composable
fun ActionRow(
    modifier: Modifier = Modifier, visible: Boolean, onActionClick: (ActionType) -> Unit
) {
    AnimatedVisibility(modifier = modifier,
        visible = visible,
        enter = slideInVertically(tween(500)) { it } + scaleIn(tween(500)),
        exit = scaleOut(tween(500)) + slideOutVertically(tween(500)) { it }) {
        Row(modifier = modifier) {
            ActionButton(
                id = R.drawable.baseline_favorite_border_24,
                type = ActionType.FAVORITE,
                onActionClick = onActionClick
            )
            ActionButton(
                id = R.drawable.baseline_download_24,
                type = ActionType.DOWNLOAD,
                onActionClick = onActionClick
            )
            ActionButton(
                id = R.drawable.baseline_share_24,
                type = ActionType.SHARE,
                onActionClick = onActionClick
            )
        }
    }
}


@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    @DrawableRes id: Int,
    type: ActionType,
    onActionClick: (ActionType) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed.value) 0.9F else 1F, label = ""
    )
    Card(
        elevation = CardDefaults.cardElevation(12.dp),
        modifier = modifier
            .padding(8.dp)
            .scale(buttonScale),
        shape = RoundedCornerShape(48.dp)
    ) {
        IconButton(
            onClick = { onActionClick(type) },
            interactionSource = interactionSource,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                ImageVector.vectorResource(id = id),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

