package com.doodle.feature.search.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.doodle.core.domain.enums.PagingKey
import com.doodle.core.domain.model.navigation.PictureDetailsNavArgs
import com.doodle.core.domain.model.navigation.SearchNavArgs
import com.doodle.core.domain.model.remote.RemoteImage
import com.doodle.core.ui.ApplicationScaffold
import com.doodle.core.ui.ErrorMessage
import com.doodle.core.ui.FetchedImageItem
import com.doodle.core.ui.LoadingBar
import com.doodle.core.ui.PagingLaunchedEffect
import com.doodle.core.ui.R
import com.doodle.core.ui.card.CardImageList
import com.doodle.core.ui.card.EmptyListContent
import com.doodle.core.ui.checkForSpecificException
import com.doodle.core.ui.scaleWithPressAnimation
import com.doodle.core.ui.tweenEasy
import com.doodle.core.ui.tweenMedium
import com.doodle.feature.search.state.SearchQueryState

@Composable
fun SearchScreen(
    viewModel: SearchScreenViewModel,
    navArgs: SearchNavArgs,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchImages = viewModel.searchImagesState.collectAsLazyPagingItems()

    LaunchedEffect(navArgs) {
        viewModel.setSearchState(navArgs)
    }


    ApplicationScaffold(title = navArgs.query.ifBlank {
        stringResource(com.doodle.feature.search.R.string.search)
    }, navigationIcon = {
        IconButton(
            onClick = onNavigateBack
        ) {
            Icon(
                Icons.Default.ArrowLeft, contentDescription = null
            )
        }
    }) { innerPadding ->
        SearchScreenContent(
            uiState = uiState,
            searchImages = searchImages,
            searchQueryState = viewModel.searchQueryState,
            onNavigateToDetails = onNavigateToDetails,
            onUpdateUiState = viewModel::updateUiState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        )
    }
}

@Composable
fun SearchScreenContent(
    modifier: Modifier = Modifier,
    uiState: SearchScreenViewModel.UiState?,
    searchImages: LazyPagingItems<RemoteImage.Hit>,
    searchQueryState: SearchQueryState,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    onUpdateUiState: (SearchScreenViewModel.UiState?) -> Unit
) {
    val context = LocalContext.current
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

    LaunchedEffect(searchQueryState.isRetrying) {
        if (searchQueryState.isRetrying) {
            searchImages.retry()
            searchQueryState.retryComplete()
            onUpdateUiState(SearchScreenViewModel.UiState.Loading)
        }
    }

    PagingLaunchedEffect(states = searchImages.loadState, onLoading = {
        val state = SearchScreenViewModel.UiState.Loading
        onUpdateUiState(state)
    }, onError = { error ->
        val msg = checkForSpecificException(context, error)
        val state = SearchScreenViewModel.UiState.Error(message = msg)
        onUpdateUiState(state)
    }, onNotLoading = {
        onUpdateUiState(null)
    })


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        SearchBarContent(uiState = uiState, searchQueryState = searchQueryState)

        CardImageList(modifier = Modifier
            .fillMaxSize()
            .weight(1F),
            columns = StaggeredGridCells.Fixed(3),
            isItemsEmpty = searchImages.itemCount == 0,
            state = listState,
            onEmptyContent = {
                EmptyListContent(
                    textPlaceholder = stringResource(id = R.string.no_images_available)
                )
            }) {
            items(searchImages.itemCount) { index ->
                val image = searchImages[index] ?: return@items
                FetchedImageItem(
                    previewURL = image.previewURL ?: "", onNavigateToDetails = {
                        onNavigateToDetails(
                            PictureDetailsNavArgs(
                                selectedImageIndex = index,
                                pagingKey = PagingKey.SEARCH,
                                query = query
                            )
                        )
                    },
                    isPremium = false,
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
    uiState: SearchScreenViewModel.UiState?,
    searchQueryState: SearchQueryState
) {
    val query by searchQueryState.query.collectAsState()
    val transition = updateTransition(targetState = searchQueryState.isFocused, label = "")
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(searchQueryState.isFocused, transition.currentState) {
        if (transition.currentState) {
            focusRequester.requestFocus()
        }
    }

    Column(
        modifier = modifier
            .animateContentSize(tweenEasy())
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            AnimatedContent(
                targetState = transition.currentState,
                transitionSpec = {
                    fadeIn(tweenMedium()) togetherWith fadeOut(tweenMedium()) using SizeTransform { _, _ ->
                        spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    }
                },
                label = "Animated Search Bar",
            ) { focused ->
                if (!focused) {
                    SearchButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        query = query,
                    ) {
                        searchQueryState.setFocus()

                    }
                } else {
                    SearchField(
                        query = query,
                        focusRequester = focusRequester,
                        onValueChange = searchQueryState::updateQuery
                    )
                }
            }
        }
        androidx.compose.animation.AnimatedVisibility(
            visible = uiState is SearchScreenViewModel.UiState.Loading,
            enter = slideInVertically(tweenEasy(easing = FastOutLinearInEasing)) { it },
            exit = slideOutVertically(tweenEasy(easing = FastOutLinearInEasing)) { it }
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
    ErrorMessage(
        (uiState as? SearchScreenViewModel.UiState.Error)?.message,
        onErrorClick = searchQueryState::retry
    )

}

@Composable
fun SearchButton(
    modifier: Modifier = Modifier, query: String, onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()


    if (query.isNotBlank()) {
        Text(
            text = query,
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
                .padding(8.dp)
                .clickable(
                    interactionSource = interactionSource, indication = null, onClick = onClick
                )
                .scaleWithPressAnimation(isPressed)
        )
    } else {
        IconButton(
            onClick = onClick,
            interactionSource = interactionSource,
            modifier = modifier
                .size(36.dp)
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
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(modifier = modifier.focusRequester(focusRequester),
        value = query,
        textStyle = MaterialTheme.typography.titleMedium,
        singleLine = true,
        onValueChange = onValueChange,
        trailingIcon = {
            IconButton(onClick = {
                onValueChange("")
            }) {
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
        })
}
