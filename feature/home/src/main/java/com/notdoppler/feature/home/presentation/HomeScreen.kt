package com.notdoppler.feature.home.presentation

import androidx.activity.ComponentActivity
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.notdoppler.core.domain.R
import com.notdoppler.core.domain.enums.PagingKey
import com.notdoppler.core.domain.model.navigation.PictureDetailsNavArgs
import com.notdoppler.core.domain.model.navigation.SearchNavArgs
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.TagData
import com.notdoppler.core.ui.ApplicationScaffold
import com.notdoppler.core.ui.FetchedImageItem
import com.notdoppler.core.ui.NavigationIcon
import com.notdoppler.core.ui.list.CardImageList
import com.notdoppler.core.ui.list.EmptyListContent
import com.notdoppler.feature.home.checkForSpecificException
import com.notdoppler.feature.home.domain.enums.TabPage
import com.notdoppler.feature.home.domain.tagCategories
import com.notdoppler.feature.home.offsetForPage
import com.notdoppler.feature.home.presentation.common.HomeModalNavigationDrawer
import com.notdoppler.feature.home.presentation.common.HomeTab
import com.notdoppler.feature.home.presentation.common.TagCard
import com.notdoppler.feature.home.state.PagingKeyState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.min

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onNavigateToFavorites: () -> Unit,
    onNavigateToSearch: (SearchNavArgs?) -> Unit,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val imageFlow by viewModel.imageFlow.collectAsStateWithLifecycle(null)

    LaunchedEffect(imageFlow) {
        viewModel.updatePagingData(imageFlow)
    }

    HomeModalNavigationDrawer(
        onNavigateToFavorites = onNavigateToFavorites,
        onShowReview = {
            val activity = context as ComponentActivity
            viewModel.requestReview(activity)
        }
    ) { drawerState ->
        ApplicationScaffold(
            title = stringResource(id = R.string.app_name),
            navigationIcon = {
                NavigationIcon(
                    drawableRes = com.notdoppler.core.ui.R.drawable.baseline_menu_24,
                    onClick = {
                        scope.launch {
                            if (drawerState.isClosed) {
                                drawerState.open()
                            } else {
                                drawerState.close()
                            }
                        }
                    }
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
                            painter = painterResource(
                                id = com.notdoppler.core.ui.R.drawable.baseline_search_24
                            ),
                            contentDescription = null
                        )
                    }
                }
            ),
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            HomeScreenContent(
                uiState = uiState,
                pagingKeyState = viewModel.pagingKeyState,
                onUpdateUiState = viewModel::updateUiState,
                onGetImageData = viewModel::getImageData,
                onGetTagData = viewModel::getTagData,
                onNavigateToDetails = onNavigateToDetails,
                onNavigateToSearch = onNavigateToSearch,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    pagingKeyState: PagingKeyState,
    onUpdateUiState: (HomeScreenViewModel.UiState?) -> Unit,
    onGetTagData: (ImmutableList<TagData>) -> Unit,
    onGetImageData: (PagingKey) -> MutableStateFlow<PagingData<FetchedImage.Hit>>?,
    onNavigateToSearch: (SearchNavArgs) -> Unit,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    uiState: HomeScreenViewModel.UiState?
) {
    val pagerState = rememberPagerState(initialPage = TabPage.LATEST.ordinal) {
        TabPage.entries.size
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HomeTab(
                modifier = Modifier.fillMaxWidth(),
                pagerState = pagerState,
                isLoading = uiState is HomeScreenViewModel.UiState.Loading,
                isError = uiState is HomeScreenViewModel.UiState.Error,
                errorMsg = (uiState as? HomeScreenViewModel.UiState.Error)?.message,
                onErrorClick = {
                    pagingKeyState.retry()
                }
            )

            HomeImageList(
                pagingKeyState = pagingKeyState,
                pagerState = pagerState,
                onNavigateToDetails = onNavigateToDetails,
                onNavigateToSearch = onNavigateToSearch,
                onGetTagData = onGetTagData,
                onGetImageData = onGetImageData,
                onUpdateUiState = onUpdateUiState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1F)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeImageList(
    pagerState: PagerState,
    pagingKeyState: PagingKeyState,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    onNavigateToSearch: (SearchNavArgs) -> Unit,
    onGetTagData: (ImmutableList<TagData>) -> Unit,
    onGetImageData: (PagingKey) -> MutableStateFlow<PagingData<FetchedImage.Hit>>?,
    onUpdateUiState: (HomeScreenViewModel.UiState?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tagImages = remember {
        mutableStateListOf<TagData>().apply {
            addAll(
                tagCategories.map { tag ->
                    TagData(
                        title = tag,
                        image = mutableStateOf(null)
                    )
                }
            )
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        val pagingKey = TabPage.entries[pagerState.currentPage].key
        pagingKeyState.update(pagingKey)

        if (pagingKey == PagingKey.TAGS) {
            val result = tagImages.any { it.image.value == null }
            if (result) onGetTagData(tagImages.toImmutableList())
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { pageIndex ->
        val pagingKey = TabPage.entries[pageIndex].key
        val imageFlow = remember(pagingKey) {
            onGetImageData(pagingKey)
        }
        val images = imageFlow?.collectAsLazyPagingItems() ?: return@HorizontalPager

        LaunchedEffect(pagingKeyState.isRetrying) {
            if (pagingKeyState.isRetrying) {
                images.retry()
                pagingKeyState.retryComplete()
                onUpdateUiState(HomeScreenViewModel.UiState.Loading)
            }
        }

        LaunchedEffect(images.loadState) {
            when (val refreshState = images.loadState.refresh) {
                is LoadState.NotLoading -> {
                    when (val appendState = images.loadState.append) {
                        is LoadState.Loading -> {
                            HomeScreenViewModel.UiState.Loading
                        }

                        is LoadState.Error -> {
                            val message = checkForSpecificException(context, appendState.error)
                            HomeScreenViewModel.UiState.Error(message = message)
                        }

                        else -> null
                    }
                }

                is LoadState.Loading -> {
                    HomeScreenViewModel.UiState.Loading
                }

                is LoadState.Error -> {
                    val message = checkForSpecificException(context, refreshState.error)
                    HomeScreenViewModel.UiState.Error(message = message)
                }

                else -> null
            }.apply(onUpdateUiState)
        }

        CardImageList(
            modifier = Modifier.graphicsLayer {
                val pageOffset = pagerState.offsetForPage(pageIndex)
                val interpolated = FastOutLinearInEasing.transform(pageOffset.absoluteValue)
                val interpolatedScale = 1F - interpolated * 0.2f
                scaleX = min(1f, interpolatedScale)
                scaleY = min(1f, interpolatedScale)
            },
            columns = StaggeredGridCells.Fixed(if (pagingKey == PagingKey.TAGS) 2 else 3),
            isItemsEmpty = images.itemCount == 0,
            onEmptyContent = {
                EmptyListContent(
                    textPlaceholder = stringResource(
                        com.notdoppler.core.ui.R.string.no_images_available
                    )
                )
            }
        ) {
            if (pagingKey != PagingKey.TAGS) {
                items(images.itemCount) { index ->
                    val image = images[index] ?: return@items
                    FetchedImageItem(
                        previewURL = image.previewURL ?: "",
                        onNavigateToDetails = {
                            val args = PictureDetailsNavArgs(index, pagingKey)
                            onNavigateToDetails(args)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                }
            } else {
                items(tagImages, key = { it.title }) { (title, image) ->
                    TagCard(
                        title = title,
                        previewURL = image.value?.previewURL ?: "",
                        onNavigateToSearch = {
                            val args = SearchNavArgs(it)
                            onNavigateToSearch(args)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                }
            }
        }
    }
}
