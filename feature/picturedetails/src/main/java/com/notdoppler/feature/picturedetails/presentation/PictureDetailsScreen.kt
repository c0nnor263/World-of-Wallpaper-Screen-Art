package com.notdoppler.feature.picturedetails.presentation

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.notdoppler.core.domain.model.FetchedImage
import com.notdoppler.core.domain.presentation.TabOrder
import com.notdoppler.core.ui.HomeScreenViewModel
import com.notdoppler.core.ui.R
import com.notdoppler.feature.picturedetails.domain.model.AnchoredDraggableInfo
import com.notdoppler.feature.picturedetails.presentation.common.AnchoredDraggableArea
import kotlin.math.roundToInt


@Composable
fun PictureDetailsScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    selectedImageIndex: Int,
    tabOrder: TabOrder,
    onNavigateBack: () -> Unit
) {
    val imageHits = viewModel.tabPagingState[tabOrder]?.collectAsLazyPagingItems() ?: return

    AnchoredDraggableArea(
        modifier = Modifier.fillMaxSize(),
        onTopEnd = onNavigateBack,
        onBottomEnd = {
            // TODO: Save image to favorites
        }
    ) { draggableInfo ->
        PictureDetailsScreenContent(selectedImageIndex, imageHits, draggableInfo)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PictureDetailsScreenContent(
    selectedImageIndex: Int,
    imageHits: LazyPagingItems<FetchedImage.Hit>,
    draggableInfo: AnchoredDraggableInfo
) {
    val pagerState = rememberPagerState(
        initialPage = selectedImageIndex
    ) { imageHits.itemCount }


    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState
        ) { pageIndex ->
            AsyncImage(
                imageHits[pageIndex]?.largeImageURL,
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
        AnimatedVisibility(modifier = Modifier.align(Alignment.BottomCenter),
            visible = draggableInfo.progress < 0.035F,
            enter = slideInVertically(tween(500)) { it } + scaleIn(tween(500)),
            exit = scaleOut(tween(500)) + slideOutVertically(tween(500)) { it }) {
            ActionRow()
        }
    }
}

@Composable
fun ActionRow(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        ActionButton(id = R.drawable.baseline_favorite_border_24)
        ActionButton(id = R.drawable.baseline_download_24)
        ActionButton(id = R.drawable.baseline_share_24)
    }
}


@Composable
fun ActionButton(modifier: Modifier = Modifier, @DrawableRes id: Int) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue =
        if (isPressed.value) 0.9F else 1F, label = ""
    )
    Card(
        elevation = CardDefaults.cardElevation(12.dp),
        modifier = modifier
            .padding(8.dp)
            .scale(buttonScale),
        shape = RoundedCornerShape(48.dp)
    ) {
        IconButton(
            onClick = {},
            interactionSource = interactionSource,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                ImageVector.vectorResource(id = id), contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ActionRowPreview() {
    ActionRow()
}