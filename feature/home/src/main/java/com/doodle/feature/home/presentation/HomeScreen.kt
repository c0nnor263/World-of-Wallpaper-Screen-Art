package com.doodle.feature.home.presentation

import androidx.activity.ComponentActivity
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.doodle.core.domain.R
import com.doodle.core.domain.enums.PagingKey
import com.doodle.core.domain.model.navigation.PictureDetailsNavArgs
import com.doodle.core.domain.model.navigation.SearchNavArgs
import com.doodle.core.domain.model.remote.RemoteImage
import com.doodle.core.domain.model.remote.TagData
import com.doodle.core.ui.ApplicationScaffold
import com.doodle.core.ui.FetchedImageItem
import com.doodle.core.ui.NavigationIcon
import com.doodle.core.ui.captureChildrenGestures
import com.doodle.core.ui.card.CardImageList
import com.doodle.core.ui.card.EmptyListContent
import com.doodle.feature.home.PagingLaunchedEffect
import com.doodle.feature.home.checkForSpecificException
import com.doodle.feature.home.domain.enums.TabPage
import com.doodle.feature.home.offsetForPage
import com.doodle.feature.home.presentation.common.HomeNavigationDrawer
import com.doodle.feature.home.presentation.common.HomeTabLayout
import com.doodle.feature.home.presentation.common.TagCard
import com.doodle.feature.home.state.LocalHomePagingState
import com.doodle.feature.home.state.rememberHomeDrawerState
import kotlin.math.absoluteValue
import kotlin.math.min

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onNavigateToFavorites: () -> Unit,
    onNavigateToSearch: (SearchNavArgs?) -> Unit,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagingImageFlow by viewModel.pagingImageFlow.collectAsStateWithLifecycle(null)
    val drawerState = rememberHomeDrawerState()

    SideEffect {
        drawerState.updateWidth()
    }

    LaunchedEffect(pagingImageFlow) {
        viewModel.cacheDataForPagingKey(pagingImageFlow)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        HomeNavigationDrawer(
            drawerState = drawerState.state,
            onNavigateToFavorites = onNavigateToFavorites,
            onShowReview = {
                val activity = context as ComponentActivity
                viewModel.requestApplicationReview(activity)
            },
            onRequestRemoveAds = {
                val activity = context as ComponentActivity
                viewModel.requestBillingRemoveAds(activity)
            }
        )
        ApplicationScaffold(
            title = stringResource(id = R.string.app_name),
            navigationIcon = {
                NavigationIcon(
                    imageVector = Icons.Default.Menu,
                    onClick = drawerState::showOrClose
                )
            },
            actions = arrayOf(
                {
                    IconButton(
                        onClick = {
                            onNavigateToSearch(null)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    }
                }
            ),
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val interpolatedScale =
                        lerp(
                            start = 1F,
                            stop = 1.2F,
                            fraction = drawerState.fraction
                        )
                    val interpolatedTransitionX =
                        lerp(
                            start = 0F,
                            stop = drawerState.width / 1.4F,
                            fraction = drawerState.fraction
                        )
                    translationX = interpolatedTransitionX
                    scaleX = interpolatedScale
                    scaleY = interpolatedScale

                    if (drawerState.state.isOpen) {
                        shadowElevation = 8F
                        shape = RoundedCornerShape(24.dp)
                        clip = true
                    }
                }
                .captureChildrenGestures(drawerState.state.isOpen) {
                    drawerState.close()
                }
        ) { innerPadding ->
            CompositionLocalProvider(LocalHomePagingState provides viewModel.homePagingState) {
                HomeScreenContent(
                    uiState = uiState,
                    onUpdateUiState = viewModel::updateUiState,
                    onNavigateToDetails = onNavigateToDetails,
                    onNavigateToSearch = onNavigateToSearch,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    onUpdateUiState: (HomeScreenViewModel.UiState?) -> Unit,
    onNavigateToSearch: (SearchNavArgs) -> Unit,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    uiState: HomeScreenViewModel.UiState?
) {
    val homePagingState = LocalHomePagingState.current
    val pagerState = rememberPagerState(initialPage = TabPage.LATEST.ordinal) {
        TabPage.entries.size
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        HomeTabLayout(
            modifier = Modifier.fillMaxWidth(),
            pagerState = pagerState,
            isLoading = uiState is HomeScreenViewModel.UiState.Loading,
            isError = uiState is HomeScreenViewModel.UiState.Error,
            errorMsg = (uiState as? HomeScreenViewModel.UiState.Error)?.message,
            onErrorClick = {
                homePagingState.retry()
            }
        )

        HomeImageList(
            pagerState = pagerState,
            onNavigateToDetails = onNavigateToDetails,
            onNavigateToSearch = onNavigateToSearch,
            onUpdateUiState = onUpdateUiState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1F)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeImageList(
    pagerState: PagerState,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    onNavigateToSearch: (SearchNavArgs) -> Unit,
    onUpdateUiState: (HomeScreenViewModel.UiState?) -> Unit,
    modifier: Modifier = Modifier
) {
    val homePagingState = LocalHomePagingState.current
    val context = LocalContext.current

    LaunchedEffect(pagerState.currentPage) {
        val pagingKey = TabPage.entries[pagerState.currentPage].key
        homePagingState.updateKey(pagingKey)
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        key = { index ->
            TabPage.entries[index].key
        }
    ) { pageIndex ->
        val pagingKey = remember { TabPage.entries[pageIndex].key }
        val pagingImageFlow = remember(pagingKey) {
            homePagingState.getPagingImageFlow(pagingKey)
        }
        val tagList = remember {
            homePagingState.tagList
        }
        val pagingRemoteImages = pagingImageFlow.collectAsLazyPagingItems()
        val imageCount =
            if (pagingKey == PagingKey.TAGS) {
                tagList.size
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
                        com.doodle.core.ui.R.string.no_images_available
                    )
                )
            }
        ) {
            when (pagingKey) {
                PagingKey.TAGS -> {
                    TagList(
                        modifier = modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        items = tagList,
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
