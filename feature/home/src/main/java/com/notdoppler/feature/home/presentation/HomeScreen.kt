package com.notdoppler.feature.home.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.notdoppler.core.domain.R
import com.notdoppler.core.domain.model.navigation.PictureDetailsNavArgs
import com.notdoppler.core.domain.model.navigation.SearchNavArgs
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.presentation.TabOrder
import com.notdoppler.core.ui.ApplicationScaffold
import com.notdoppler.core.ui.ImageCard
import com.notdoppler.core.ui.LoadingBar
import com.notdoppler.feature.home.domain.tabInfo
import com.notdoppler.feature.home.presentation.common.HomeModalNavigationDrawer
import com.notdoppler.feature.home.presentation.common.TabImages
import com.notdoppler.feature.home.state.TabOrderState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onNavigateToSearch: (SearchNavArgs?) -> Unit,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
) {
    val mapPagingData = viewModel.mapPagingData
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.observeImagesFlow()
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is HomeScreenViewModel.UiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
            }

            else -> {}
        }
    }


    HomeModalNavigationDrawer { drawerState ->
        ApplicationScaffold(
            title = stringResource(id = R.string.app_name),
            navigationIcon = { NavigationIcon(drawerState) },
            actions = arrayOf(
                {
                    IconButton(
                        onClick = {
                            onNavigateToSearch(null)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = com.notdoppler.core.ui.R.drawable.baseline_search_24),
                            contentDescription = null
                        )
                    }
                }
            ),
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            HomeScreenContent(
                uiState = uiState,
                mapPagingData = mapPagingData,
                tabOrderState = viewModel.tabOrderState,
                onUpdateUiState = viewModel::updateUiState,
                onNavigateToDetails = onNavigateToDetails,
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
    mapPagingData: SnapshotStateMap<TabOrder, MutableStateFlow<PagingData<FetchedImage.Hit>>?>,
    tabOrderState: TabOrderState,
    onUpdateUiState: (HomeScreenViewModel.UiState?) -> Unit,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    uiState: HomeScreenViewModel.UiState?,
) {
    val pagerState = rememberPagerState { tabInfo.size }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
        ) {
            TabImages(modifier = Modifier.fillMaxWidth(), pagerState = pagerState)

            CardImageList(
                mapPagingData = mapPagingData,
                tabOrderState = tabOrderState,
                pagerState = pagerState,
                onNavigateToDetails = onNavigateToDetails,
                onUpdateUiState = onUpdateUiState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1F)
            )
        }
        LoadingBar(visible = uiState == HomeScreenViewModel.UiState.Loading)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardImageList(
    mapPagingData: SnapshotStateMap<TabOrder, MutableStateFlow<PagingData<FetchedImage.Hit>>?>,
    pagerState: PagerState,
    tabOrderState: TabOrderState,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    onUpdateUiState: (HomeScreenViewModel.UiState?) -> Unit,
    modifier: Modifier = Modifier,
) {


    LaunchedEffect(pagerState.currentPage) {
        val tabOrder = tabInfo[pagerState.currentPage].order
        tabOrderState.update(tabOrder)
    }


    HorizontalPager(state = pagerState) { pageIndex ->
        val tabInfo = tabInfo[pageIndex]
        val images =
            mapPagingData[tabInfo.order]?.collectAsLazyPagingItems() ?: return@HorizontalPager

        LaunchedEffect(images.loadState) {
            val newState =
                when (val state = images.loadState.refresh) {
                    is LoadState.NotLoading -> null
                    is LoadState.Loading -> {
                        HomeScreenViewModel.UiState.Loading
                    }

                    is LoadState.Error -> {
                        HomeScreenViewModel.UiState.Error(message = state.error.message ?: "")
                    }
                }
            onUpdateUiState(newState)

        }


        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(3),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier
        ) {
            items(images.itemCount) { index ->
                ImageCard(
                    image = images[index] ?: return@items,
                    onNavigateToDetails = {
                        onNavigateToDetails(PictureDetailsNavArgs(index, tabInfo.order))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                )
            }
        }
    }
}


@Composable
fun NavigationIcon(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    IconButton(
        onClick = {
            scope.launch {
                if (drawerState.isClosed) {
                    drawerState.open()
                } else {
                    drawerState.close()
                }
            }
        }
    ) {
        Icon(
            painter = painterResource(id = com.notdoppler.core.ui.R.drawable.baseline_menu_24),
            contentDescription = null
        )
    }
}