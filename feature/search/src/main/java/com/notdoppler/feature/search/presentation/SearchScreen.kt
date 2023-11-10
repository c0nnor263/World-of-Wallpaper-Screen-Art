package com.notdoppler.feature.search.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.notdoppler.core.domain.enums.PagingKey
import com.notdoppler.core.domain.model.navigation.PictureDetailsNavArgs
import com.notdoppler.core.domain.model.navigation.SearchNavArgs
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.ui.ApplicationScaffold
import com.notdoppler.core.ui.CardImageList
import com.notdoppler.core.ui.FetchedImageItem
import com.notdoppler.core.ui.LoadingBar
import com.notdoppler.feature.search.state.SearchQueryState

@Composable
fun SearchScreen(
    viewModel: SearchScreenViewModel,
    navArgs: SearchNavArgs,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    onNavigateBack: () -> Unit,
) {

    LaunchedEffect(navArgs) {
        viewModel.setSearchState(navArgs)
    }

    val images = viewModel.imageState.collectAsLazyPagingItems()

    ApplicationScaffold(
        title = navArgs.query.ifBlank { "Search" },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack
            ) {
                Icon(
                    painter = painterResource(id = com.notdoppler.core.ui.R.drawable.baseline_arrow_left_24),
                    contentDescription = null
                )
            }
        }
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
    val query by searchQueryState.query.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        SearchField(searchQueryState = searchQueryState)
        CardImageList(
            modifier = Modifier
                .fillMaxSize()
                .weight(1F),
            columns = StaggeredGridCells.Fixed(3)
        ) {
            items(images.itemCount) { index ->
                val image = images[index] ?: return@items
                FetchedImageItem(
                    previewURL = image.previewURL ?: "",
                    onNavigateToDetails = {
                        onNavigateToDetails(
                            PictureDetailsNavArgs(
                                selectedImageIndex = index,
                                pagingKey = PagingKey.SEARCH,
                                additionalKey = query
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                )
            }
        }
    }
    LoadingBar(visible = searchQueryState.isSearching)
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
        singleLine = true,
        trailingIcon = {
            Icon(
                painter = painterResource(id = com.notdoppler.core.ui.R.drawable.baseline_clear_24),
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { searchQueryState.updateQuery("") }
                    .padding(4.dp)
            )
        },
        onValueChange = searchQueryState::updateQuery,
        placeholder = { Text(text = "Search") }
    )
}