package com.notdoppler.feature.home.presentation

import androidx.activity.ComponentActivity
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.notdoppler.core.ui.CardImageList
import com.notdoppler.core.ui.FetchedImageItem
import com.notdoppler.core.ui.NavigationIcon
import com.notdoppler.feature.home.domain.enums.TabPage
import com.notdoppler.feature.home.domain.tagCategories
import com.notdoppler.feature.home.presentation.common.HomeModalNavigationDrawer
import com.notdoppler.feature.home.presentation.common.TabImages
import com.notdoppler.feature.home.presentation.common.TagCard
import com.notdoppler.feature.home.state.PagingKeyState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onNavigateToFavorites: () -> Unit,
    onNavigateToSearch: (SearchNavArgs?) -> Unit,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val mapPagingData = viewModel.mapPagingData
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val imagesFlow by viewModel.imagesFlow.collectAsStateWithLifecycle(null)

    LaunchedEffect(imagesFlow) {
        viewModel.updatePagingData(imagesFlow)
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is HomeScreenViewModel.UiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
            }

            else -> {}
        }
    }


    HomeModalNavigationDrawer(onNavigateToFavorites = onNavigateToFavorites, onShowReview = {
        val activity = context as ComponentActivity
        viewModel.requestReview(activity)
    }) { drawerState ->
        ApplicationScaffold(
            title = stringResource(id = R.string.app_name),
            navigationIcon = {
                NavigationIcon(drawableRes = com.notdoppler.core.ui.R.drawable.baseline_menu_24,
                    onClick = {
                        scope.launch {
                            if (drawerState.isClosed) {
                                drawerState.open()
                            } else {
                                drawerState.close()
                            }
                        }
                    })
            },
            actions = arrayOf({
                IconButton(onClick = {
                    onNavigateToSearch(null)
                }) {
                    Icon(
                        painter = painterResource(id = com.notdoppler.core.ui.R.drawable.baseline_search_24),
                        contentDescription = null
                    )
                }
            }),
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            HomeScreenContent(
                uiState = uiState,
                mapPagingData = mapPagingData,
                pagingKeyState = viewModel.pagingKeyState,
                onUpdateUiState = viewModel::updateUiState,
                onNavigateToDetails = onNavigateToDetails,
                onNavigateToSearch = onNavigateToSearch,
                onGetTagData = { tagList ->
                    viewModel.getTagImages(tagList)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    mapPagingData: SnapshotStateMap<PagingKey, MutableStateFlow<PagingData<FetchedImage.Hit>>?>,
    pagingKeyState: PagingKeyState,
    onUpdateUiState: (HomeScreenViewModel.UiState?) -> Unit,
    onGetTagData: suspend (ImmutableList<TagData>) -> Unit,
    onNavigateToSearch: (SearchNavArgs) -> Unit,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    uiState: HomeScreenViewModel.UiState?,
) {
    val pagerState =
        rememberPagerState(initialPage = TabPage.LATEST.ordinal) {
            TabPage.entries.size
        }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
        ) {
            TabImages(
                modifier = Modifier.fillMaxWidth(),
                pagerState = pagerState,
                isLoading = uiState == HomeScreenViewModel.UiState.Loading
            )

            HomeImageList(
                mapPagingData = mapPagingData,
                pagingKeyState = pagingKeyState,
                pagerState = pagerState,
                onNavigateToDetails = onNavigateToDetails,
                onNavigateToSearch = onNavigateToSearch,
                onGetTagData = onGetTagData,
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
    mapPagingData: SnapshotStateMap<PagingKey, MutableStateFlow<PagingData<FetchedImage.Hit>>?>,
    pagerState: PagerState,
    pagingKeyState: PagingKeyState,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    onNavigateToSearch: (SearchNavArgs) -> Unit,
    onGetTagData: suspend (ImmutableList<TagData>) -> Unit,
    onUpdateUiState: (HomeScreenViewModel.UiState?) -> Unit,
    modifier: Modifier = Modifier,
) {

    val tagImages = remember {
        mutableStateListOf<TagData>().apply {
            addAll(tagCategories.map { tag ->
                TagData(
                    title = tag, image = mutableStateOf(null)
                )
            })
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


    HorizontalPager(state = pagerState, modifier = modifier) { pageIndex ->
        val pagingKey = TabPage.entries[pageIndex].key
        val images =
            mapPagingData[pagingKey]?.collectAsLazyPagingItems() ?: return@HorizontalPager


        // Fix loading state
        LaunchedEffect(images.loadState) {
            val newState = when (val state = images.loadState.refresh) {
                is LoadState.NotLoading -> null
                is LoadState.Loading -> {
                    HomeScreenViewModel.UiState.Loading
                }

                is LoadState.Error -> {
                    HomeScreenViewModel.UiState.Error(message = state.error.message ?: "")
                }

                else -> null
            }
            onUpdateUiState(newState)
        }


        CardImageList(
            columns = StaggeredGridCells.Fixed(if (pagingKey == PagingKey.TAGS) 2 else 3),
        ) {
            if (pagingKey != PagingKey.TAGS) {
                items(images.itemCount) { index ->
                    val image = images[index] ?: return@items
                    FetchedImageItem(
                        previewURL = image.previewURL ?: "",
                        onNavigateToDetails = {
                            onNavigateToDetails(PictureDetailsNavArgs(index, pagingKey))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                    )
                }
            } else {
                items(tagImages, key = { it.title }) { (title, image) ->
                    TagCard(
                        title = title,
                        previewURL = image.value?.previewURL ?: "",
                        onNavigateToSearch = {
                            onNavigateToSearch(SearchNavArgs(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                    )
                }

            }

        }
    }
}