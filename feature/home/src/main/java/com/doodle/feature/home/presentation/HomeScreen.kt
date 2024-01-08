package com.doodle.feature.home.presentation

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doodle.core.domain.R
import com.doodle.core.domain.model.navigation.PictureDetailsNavArgs
import com.doodle.core.domain.model.navigation.SearchNavArgs
import com.doodle.core.ui.ApplicationScaffold
import com.doodle.core.ui.NavigationIcon
import com.doodle.core.ui.captureChildrenGestures
import com.doodle.core.ui.state.rememberDialogState
import com.doodle.feature.home.domain.enums.TabPage
import com.doodle.feature.home.presentation.common.HomeNavigationDrawer
import com.doodle.feature.home.presentation.common.HomePager
import com.doodle.feature.home.presentation.common.HomeTabLayout
import com.doodle.feature.home.presentation.common.RequestNotificationPermissionDialog
import com.doodle.feature.home.state.LocalHomePagingState
import com.doodle.feature.home.state.rememberHomeDrawerState

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
    val isRequestNotificationDialog = rememberDialogState()

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


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        RequestNotificationPermissionDialog(
            state = isRequestNotificationDialog,
            onDismiss = {
                isRequestNotificationDialog.dismiss()
            }
        )
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

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeTabLayout(
            modifier = Modifier.fillMaxWidth(),
            pagerState = pagerState,
            isLoading = uiState is HomeScreenViewModel.UiState.Loading,
            errorMsg = (uiState as? HomeScreenViewModel.UiState.Error)?.message,
            onErrorClick = {
                homePagingState.retry()
            }
        )

        HomePager(
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
