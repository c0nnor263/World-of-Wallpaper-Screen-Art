package com.notdoppler.feature.search.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.notdoppler.core.domain.model.PictureDetailsNavArgs
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.presentation.TabOrder
import com.notdoppler.core.ui.ImageCard
import com.notdoppler.feature.search.state.SearchQueryState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchScreenViewModel,
    query: String?,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    onNavigateBack: () -> Unit,
) {

    LaunchedEffect(key1 = Unit, block = {
        viewModel.setQuery(query)
    })

    val images = viewModel.imageState.collectAsLazyPagingItems()


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = query ?: "Search") },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            painter = painterResource(id = com.notdoppler.core.ui.R.drawable.baseline_menu_24),
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.displayCutoutPadding()
            )
        }, modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        SearchScreenContent(
            images = images,
            searchQueryState = viewModel.searchQueryState,
            onNavigateToDetails = onNavigateToDetails,
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        )
    }
}

@Composable
fun SearchScreenContent(
    images: LazyPagingItems<FetchedImage.Hit>,
    searchQueryState: SearchQueryState,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,

    modifier: Modifier = Modifier,

    ) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {

        SearchField(searchQueryState = searchQueryState)

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
                        onNavigateToDetails(PictureDetailsNavArgs(index, TabOrder.LATEST))
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
fun SearchField(
    modifier: Modifier = Modifier,
    searchQueryState: SearchQueryState,
) {
    val query by searchQueryState.query.collectAsState()
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = query,
        leadingIcon = {
            Icon(
                painterResource(id = com.notdoppler.core.ui.R.drawable.baseline_search_24),
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
        },
        onValueChange = searchQueryState::updateSearchQuery,
        placeholder = { Text(text = "Search") }
    )

}

@Preview
@Composable
fun SearchFieldPreview() {
    val searchQuery = remember { mutableStateOf("") }
    SearchField(searchQueryState = SearchQueryState())
}
