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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.notdoppler.core.domain.model.FetchedImage
import com.notdoppler.feature.home.domain.tabInfo
import com.notdoppler.feature.home.presentation.common.HomeModalNavigationDrawer
import com.notdoppler.feature.home.presentation.common.HomeScreenScaffold
import com.notdoppler.feature.home.presentation.image.ImageCard

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onNavigateToDetails: (FetchedImage.Hit?) -> Unit
) {
    val images = viewModel.imagesState.collectAsLazyPagingItems()
    HomeModalNavigationDrawer { drawerState ->
        HomeScreenScaffold(drawerState = drawerState) { innerPadding ->
            HomeScreenContent(
                images = images,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                onNavigateToDetails = onNavigateToDetails
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    images: LazyPagingItems<FetchedImage.Hit>,
    onNavigateToDetails: (FetchedImage.Hit?) -> Unit
) {
    val pagerState = rememberPagerState {
        tabInfo.size
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        TabImages(modifier = Modifier.fillMaxWidth())
            // TODO: Complete the implementation of the TabImages composable
        HorizontalPager(state = pagerState) { page ->
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
                        onNavigateToDetails = onNavigateToDetails,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                    )
                }
            }
        }
    }
}


@Composable
private fun TabImages(modifier: Modifier = Modifier) {
    var currentTab by remember { mutableIntStateOf(0) }

    ScrollableTabRow(
        modifier = modifier,
        selectedTabIndex = currentTab,
        edgePadding = 0.dp,
    ) {
        tabInfo.forEachIndexed { index, tabInfo ->
            Tab(
                text = { Text(text = tabInfo.title) },
                selected = currentTab == index,
                onClick = {
                    currentTab = index
                }
            )
        }
    }
}


