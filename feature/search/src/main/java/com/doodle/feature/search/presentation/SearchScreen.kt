package com.doodle.feature.search.presentation

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.doodle.core.domain.enums.PagingKey
import com.doodle.core.domain.model.navigation.PictureDetailsNavArgs
import com.doodle.core.domain.model.navigation.SearchNavArgs
import com.doodle.core.domain.model.remote.RemoteImage
import com.doodle.core.ui.ApplicationScaffold
import com.doodle.core.ui.FetchedImageItem
import com.doodle.core.ui.LoadingBar
import com.doodle.core.ui.R
import com.doodle.core.ui.card.CardImageList
import com.doodle.core.ui.card.EmptyListContent
import com.doodle.core.ui.scaleWithPressAnimation
import com.doodle.core.ui.theme.WallpapersTheme
import com.doodle.feature.search.state.SearchQueryState
import kotlinx.coroutines.flow.flow

@Composable
fun SearchScreen(
    viewModel: SearchScreenViewModel,
    navArgs: SearchNavArgs,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(navArgs) {
        viewModel.setSearchState(navArgs)
    }

    val images = viewModel.imageState.collectAsLazyPagingItems()

    ApplicationScaffold(
        title = navArgs.query.ifBlank {
            stringResource(com.doodle.feature.search.R.string.search)
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack
            ) {
                Icon(
                    Icons.Default.ArrowLeft,
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
                .padding(innerPadding)
        )
    }
}

@Composable
fun SearchScreenContent(
    images: LazyPagingItems<RemoteImage.Hit>,
    searchQueryState: SearchQueryState,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    modifier: Modifier = Modifier
) {
    val query by searchQueryState.query.collectAsState()
    val listState = rememberLazyStaggeredGridState()
    val clearFocus by remember {
        derivedStateOf {
            listState.isScrollInProgress
        }
    }

    LaunchedEffect(clearFocus) {
        if (clearFocus) {
            searchQueryState.clearFocus()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        SearchBarContent(searchQueryState = searchQueryState)

        CardImageList(
            modifier = Modifier
                .fillMaxSize()
                .weight(1F),
            columns = StaggeredGridCells.Fixed(3),
            isItemsEmpty = images.itemCount == 0,
            state = listState,
            onEmptyContent = {
                EmptyListContent(
                    textPlaceholder = stringResource(id = R.string.no_images_available)
                )
            }
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
                                query = query
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
            }
        }
    }
    LoadingBar(visible = searchQueryState.isSearching)
}

@Composable
fun SearchBarContent(
    modifier: Modifier = Modifier,
    searchQueryState: SearchQueryState
) {
    val query by searchQueryState.query.collectAsState()

    val transition = updateTransition(targetState = searchQueryState.isFocused, label = "")

    Card(
        modifier = modifier.padding(8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    ) {
        AnimatedContent(
            targetState = transition.targetState,
            transitionSpec = {
                fadeIn() togetherWith fadeOut() using
                    SizeTransform { _, _ ->
                        spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    }
            },
            label = "Animated Search Bar"
        ) { focused ->
            if (!focused) {
                SearchButton(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    query = query
                ) {
                    searchQueryState.setFocus()
                }
            } else {
                SearchField(
                    query = query,
                    onValueChange = searchQueryState::updateQuery
                )
            }
        }
    }
}

@Composable
fun SearchButton(
    modifier: Modifier = Modifier,
    query: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    if (query.isNotBlank()) {
        Text(
            text = query,
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
                .padding(4.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .scaleWithPressAnimation(isPressed)
        )
    } else {
        IconButton(
            onClick = onClick,
            interactionSource = interactionSource,
            modifier = modifier.size(36.dp)
                .scaleWithPressAnimation(isPressed)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)

            )
        }
    }
}

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    query: String,
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(
        modifier = modifier,
        value = query,
        textStyle = MaterialTheme.typography.titleMedium,
        singleLine = true,
        onValueChange = onValueChange,
        trailingIcon = {
            IconButton(
                onClick = {
                    onValueChange("")
                }
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }
        },
        placeholder = {
            Text(
                text = stringResource(
                    id = com.doodle.feature.search.R.string.search
                )
            )
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun SearchFieldPreview() {
    WallpapersTheme {
        val items = flow<PagingData<RemoteImage.Hit>> {
            PagingData.empty<RemoteImage.Hit>()
        }.collectAsLazyPagingItems()
        SearchScreenContent(
            images = items,
            searchQueryState = SearchQueryState(),
            onNavigateToDetails = {}
        )
    }
}
