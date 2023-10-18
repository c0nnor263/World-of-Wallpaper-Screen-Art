package com.notdoppler.feature.home.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.notdoppler.core.domain.model.PictureDetailsNavArgs
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.ui.ImageCard
import com.notdoppler.feature.home.domain.tabInfo
import com.notdoppler.feature.home.presentation.common.HomeModalNavigationDrawer
import com.notdoppler.feature.home.presentation.common.HomeScreenScaffold
import com.notdoppler.feature.home.presentation.common.TabImages
import com.notdoppler.feature.home.state.TabOrderState

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onNavigateToSearch: (String?) -> Unit,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
) {

    val images = viewModel.imageState.collectAsLazyPagingItems()
// TODO fetching data loading indicator
    HomeModalNavigationDrawer { drawerState ->
        HomeScreenScaffold(
            drawerState = drawerState,
            onNavigateToSearch = onNavigateToSearch
        ) { innerPadding ->
            HomeScreenContent(
                images = images,
                tabOrderState = viewModel.tabOrderState,
                onNavigateToDetails = onNavigateToDetails,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    images: LazyPagingItems<FetchedImage.Hit>,
    tabOrderState: TabOrderState,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
) {

    val pagerState = rememberPagerState { tabInfo.size }

    LaunchedEffect(pagerState.currentPage) {
        val tabOrder = tabInfo[pagerState.currentPage].order
        tabOrderState.update(tabOrder)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        TabImages(modifier = Modifier.fillMaxWidth(), pagerState = pagerState)

        HorizontalPager(state = pagerState) { pageIndex ->
            val tabInfo = tabInfo[pageIndex]
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(3),
                verticalItemSpacing = 4.dp,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1F)
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
}
