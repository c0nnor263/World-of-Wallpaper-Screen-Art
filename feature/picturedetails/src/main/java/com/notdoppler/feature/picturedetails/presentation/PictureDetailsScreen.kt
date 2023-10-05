package com.notdoppler.feature.picturedetails.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.notdoppler.core.domain.model.FetchedImage
import com.notdoppler.core.ui.R
import com.notdoppler.feature.picturedetails.domain.model.AnchoredDraggableInfo
import com.notdoppler.feature.picturedetails.presentation.common.AnchoredDraggableArea
import kotlin.math.roundToInt


@Composable
fun PictureDetailsScreen(
    viewModel: PictureDetailsViewModel = hiltViewModel(),
    selectedImageIndex: Int,
    onNavigateBack: () -> Unit
) {

    LaunchedEffect(Unit){
        viewModel.getPagerImages(selectedImageIndex)
    }

    val imageHits = viewModel.imagesState.collectAsLazyPagingItems()

    AnchoredDraggableArea(
        modifier = Modifier.fillMaxSize(),
        onEnd = onNavigateBack
    ) { draggableInfo ->
        PictureDetailsScreenContent(imageHits, draggableInfo)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PictureDetailsScreenContent(
    imageHits: LazyPagingItems<FetchedImage.Hit>,
    draggableInfo: AnchoredDraggableInfo
) {
    val pagerState = rememberPagerState {
        imageHits.
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        HorizontalPager(state = pagerState, key = { imageHits[pagerState.currentPage]?.id.toString() }) { index ->
            AsyncImage(
                imageHits[index]?.largeImageURL,
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
                    .anchoredDraggable(draggableInfo.state, Orientation.Vertical)
            )
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = draggableInfo.progress < 0.025F
        ) {
            ActionRow()
        }
    }
}

@Composable
fun ActionRow(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        IconButton(onClick = {}) {
            Icon(
                ImageVector.vectorResource(id = R.drawable.baseline_favorite_border_24),
                contentDescription = null
            )
        }
        IconButton(onClick = {}) {
            Icon(
                ImageVector.vectorResource(id = R.drawable.baseline_download_24),
                contentDescription = null
            )
        }
        IconButton(onClick = {}) {
            Icon(
                ImageVector.vectorResource(id = R.drawable.baseline_share_24),
                contentDescription = null
            )
        }
    }
}

