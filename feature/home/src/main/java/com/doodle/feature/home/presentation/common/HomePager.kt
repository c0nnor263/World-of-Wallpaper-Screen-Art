package com.doodle.feature.home.presentation.common

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.doodle.core.domain.enums.PagingKey
import com.doodle.core.domain.model.navigation.PictureDetailsNavArgs
import com.doodle.core.domain.model.navigation.SearchNavArgs
import com.doodle.core.domain.model.remote.RemoteImage
import com.doodle.core.domain.model.remote.TagData
import com.doodle.core.ui.FetchedImageItem
import com.doodle.core.ui.R
import com.doodle.core.ui.card.CardImageList
import com.doodle.core.ui.card.EmptyListContent
import com.doodle.feature.home.PagingLaunchedEffect
import com.doodle.feature.home.checkForSpecificException
import com.doodle.feature.home.domain.enums.TabPage
import com.doodle.feature.home.offsetForPage
import com.doodle.feature.home.presentation.HomeScreenViewModel
import com.doodle.feature.home.state.LocalHomePagingState
import kotlin.math.absoluteValue
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    onNavigateToSearch: (SearchNavArgs) -> Unit,
    onUpdateUiState: (HomeScreenViewModel.UiState?) -> Unit
) {
    val homePagingState = LocalHomePagingState.current

    LaunchedEffect(pagerState.currentPage) {
        val pagingKey = TabPage.entries[pagerState.currentPage].pagingKey
        homePagingState.updateKey(pagingKey)
    }

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        key = { index ->
            TabPage.entries[index].labelRes
        }
    ) { pageIndex ->
        PagerContent(
            pagerState = pagerState,
            pageIndex = pageIndex,
            onNavigateToDetails = onNavigateToDetails,
            onNavigateToSearch = onNavigateToSearch,
            onUpdateUiState = onUpdateUiState
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerContent(
    pagerState: PagerState,
    pageIndex: Int,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    onNavigateToSearch: (SearchNavArgs) -> Unit,
    onUpdateUiState: (HomeScreenViewModel.UiState?) -> Unit
) {
    val context = LocalContext.current
    val homePagingState = LocalHomePagingState.current
    val pagingKey = remember { TabPage.entries[pageIndex].pagingKey }
    val pagingImageFlow = remember {
        homePagingState.getPagingImageFlow(pagingKey)
    }
    val pagingRemoteImages = pagingImageFlow.collectAsLazyPagingItems()
    val imageCount =
        if (pagingKey == PagingKey.TAGS) {
            homePagingState.tagList.size
        } else {
            pagingRemoteImages.itemCount
        }

    LaunchedEffect(homePagingState.isRetrying) {
        if (homePagingState.isRetrying) {
            pagingRemoteImages.retry()
            homePagingState.retryComplete()
            onUpdateUiState(HomeScreenViewModel.UiState.Loading)
        }
    }

    PagingLaunchedEffect(
        states = pagingRemoteImages.loadState,
        onLoading = {
            val state = HomeScreenViewModel.UiState.Loading
            onUpdateUiState(state)
        },
        onError = { error ->
            val msg = checkForSpecificException(context, error)
            val state = HomeScreenViewModel.UiState.Error(message = msg)
            onUpdateUiState(state)
        },
        onNotLoading = {
            onUpdateUiState(null)
        }
    )

    CardImageList(
        modifier = Modifier.graphicsLayer {
            val pageOffset = pagerState.offsetForPage(pageIndex)
            val interpolated = FastOutLinearInEasing.transform(pageOffset.absoluteValue)
            val interpolatedScale = 1F - interpolated * 0.2f
            scaleX = min(1f, interpolatedScale)
            scaleY = min(1f, interpolatedScale)
        },
        columns = StaggeredGridCells.Fixed(if (pagingKey == PagingKey.TAGS) 2 else 3),
        isItemsEmpty = imageCount == 0,
        onEmptyContent = {
            EmptyListContent(
                textPlaceholder = stringResource(
                    R.string.no_images_available
                )
            )
        }
    ) {
        when (pagingKey) {
            PagingKey.TAGS -> {
                TagList(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    items = homePagingState.tagList,
                    onNavigateToSearch = {
                        val args = SearchNavArgs(it)
                        onNavigateToSearch(args)
                    }
                )
            }

            else -> {
                ImageList(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    items = pagingRemoteImages,
                    onNavigateToDetails = { index ->
                        val args = PictureDetailsNavArgs(index, pagingKey)
                        onNavigateToDetails(args)
                    }
                )
            }
        }
    }
}

fun LazyStaggeredGridScope.TagList(
    items: SnapshotStateList<TagData>,
    onNavigateToSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    items(items, key = { it.title }) { (title, image) ->
        TagCard(
            title = title,
            previewURL = image.value?.largeImageURL ?: "",
            onNavigateToSearch = onNavigateToSearch,
            modifier = modifier
        )
    }
}

fun LazyStaggeredGridScope.ImageList(
    items: LazyPagingItems<RemoteImage.Hit>,
    onNavigateToDetails: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    items(items.itemCount) { index ->
        val image = items[index]
        FetchedImageItem(
            previewURL = image?.previewURL ?: "",
            onNavigateToDetails = { onNavigateToDetails(index) },
            modifier = modifier
        )
    }
}
