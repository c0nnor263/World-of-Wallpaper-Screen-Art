package com.doodle.feature.home.presentation

import android.Manifest
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doodle.core.advertising.enums.RewardedAdResult
import com.doodle.core.advertising.rememberRewardedAdViewState
import com.doodle.core.domain.R
import com.doodle.core.domain.enums.isNotPurchased
import com.doodle.core.domain.model.navigation.PictureDetailsNavArgs
import com.doodle.core.domain.model.navigation.SearchNavArgs
import com.doodle.core.ui.ApplicationScaffold
import com.doodle.core.ui.LoadingBar
import com.doodle.core.ui.NavigationIcon
import com.doodle.core.ui.RequestPermissionDialog
import com.doodle.core.ui.captureChildrenGestures
import com.doodle.core.ui.card.CardButton
import com.doodle.core.ui.state.LocalRemoveAdsStatus
import com.doodle.core.ui.state.rememberDialogState
import com.doodle.core.ui.tweenMedium
import com.doodle.feature.home.domain.enums.TabPage
import com.doodle.feature.home.presentation.common.HomeNavigationDrawer
import com.doodle.feature.home.presentation.common.HomePager
import com.doodle.feature.home.presentation.common.HomeTabLayout
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
    val showLoadingDialogState = rememberDialogState()

    val isRequiredPermissionGranted = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    val requestNotificationDialog = rememberDialogState(!isRequiredPermissionGranted)

    SideEffect {
        // Set the drawer width to be 1/4 of the screen width
        drawerState.updateWidth()
    }

    LaunchedEffect(pagingImageFlow) {
        // Cache the data for paging when the pagingImageFlow with PagingKey is changed
        viewModel.cacheDataForPagingKey(pagingImageFlow)
    }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        HomeNavigationDrawer(
            drawerState = drawerState.state,
            onNavigateToFavorites = onNavigateToFavorites,
            onShowReview = {
                val activity = context as ComponentActivity
                viewModel.requestApplicationReview(activity)
            },
            onRequestRemoveAds = {
                showLoadingDialogState.showFor(10)
                val activity = context as ComponentActivity
                viewModel.requestBillingRemoveAds(activity, onError = {
                    val msg = context.getString(
                        com.doodle.core.ui.R.string.something_went_wrong
                    )
                    viewModel.updateUiState(HomeScreenViewModel.UiState.Error(msg))
                    showLoadingDialogState.dismiss()
                })
            },
            onRestorePurchases = {
                showLoadingDialogState.showFor(5)
                viewModel.restorePurchases()
            },
        )
        ApplicationScaffold(title = stringResource(id = R.string.app_name), navigationIcon = {
            NavigationIcon(
                imageVector = Icons.Default.Menu, onClick = drawerState::showOrClose
            )
        }, actions = arrayOf({
            IconButton(onClick = {
                onNavigateToSearch(null)
            }) {
                Icon(
                    imageVector = Icons.Default.Search, contentDescription = null
                )
            }
        }), modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                val interpolatedScale = lerp(
                    start = 1F, stop = 1.2F, fraction = drawerState.fraction
                )
                val interpolatedTransitionX = lerp(
                    start = 0F, stop = drawerState.width / 1.4F, fraction = drawerState.fraction
                )
                translationX = interpolatedTransitionX
                scaleX = interpolatedScale
                scaleY = interpolatedScale

                if (drawerState.state.targetValue == DrawerValue.Open || drawerState.state.isAnimationRunning) {
                    spotShadowColor = Color.White
                    ambientShadowColor = Color.White
                    shadowElevation = 24F
                    shape = RoundedCornerShape(24.dp)
                    clip = true
                }
            }
            .captureChildrenGestures(drawerState.state.isOpen) {
                drawerState.close()
            }) { innerPadding ->
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

    WatchAdForWallpaper(
        args = (uiState as? HomeScreenViewModel.UiState.Premium)?.args,
        onWatched = { args ->
            args?.let { onNavigateToDetails(it) }
            viewModel.updateUiState(null)
        },
        onError = {
            val msg = context.getString(com.doodle.core.ui.R.string.something_went_wrong)
            viewModel.updateUiState(HomeScreenViewModel.UiState.Error(msg))
        },
        onDismiss = {
            viewModel.updateUiState(null)
        },
    )


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        RequestPermissionDialog(state = requestNotificationDialog,
            permission = Manifest.permission.POST_NOTIFICATIONS,
            requestTitleMessage = com.doodle.core.ui.R.string.request_notification_permission_title,
            requestContentMessage = com.doodle.core.ui.R.string.request_notification_permission_message,
            onDismiss = {
                requestNotificationDialog.dismiss()
            })
    }

    LoadingBar(visible = showLoadingDialogState.isVisible)
}

@Composable
fun WatchAdForWallpaper(
    modifier: Modifier = Modifier,
    args: PictureDetailsNavArgs?,
    onWatched: (PictureDetailsNavArgs?) -> Unit,
    onError: () -> Unit,
    onDismiss: () -> Unit
) {
    val removeAdStatus = LocalRemoveAdsStatus.current
    val activity = LocalContext.current as ComponentActivity
    val rewardedInterstitialAd = rememberRewardedAdViewState(
        activity
    )
    val isShowingAdDialog = rememberDialogState()

    LaunchedEffect(isShowingAdDialog.isVisible) {
        if (isShowingAdDialog.isVisible) {
            rewardedInterstitialAd.showAd(activity) { result ->
                when (result) {
                    RewardedAdResult.REWARDED -> {
                        onWatched(args)
                    }

                    RewardedAdResult.ERROR -> onError()

                    else -> onDismiss()
                }
                isShowingAdDialog.dismiss()
            }
        }
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = args != null,
        enter = fadeIn(tweenMedium()) + scaleIn(tweenMedium()),
        exit = scaleOut(tweenMedium()) + fadeOut()
    ) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = stringResource(com.doodle.feature.home.R.string.watch_ad_for_wallpaper_title),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CardButton(
                            onClick = {
                                if (removeAdStatus.isNotPurchased()) {
                                    isShowingAdDialog.show()
                                } else {
                                    onWatched(args)
                                }
                                isShowingAdDialog.show()
                            },
                        ) {
                            Text(
                                stringResource(com.doodle.feature.home.R.string.show_ad),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                    }
                }
            }
        }
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
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeTabLayout(modifier = Modifier.fillMaxWidth(),
            pagerState = pagerState,
            isLoading = uiState is HomeScreenViewModel.UiState.Loading,
            errorMsg = (uiState as? HomeScreenViewModel.UiState.Error)?.message,
            onErrorClick = {
                homePagingState.retry()
            })

        val primary = MaterialTheme.colorScheme.primary.copy(0.1F)
        val secondary = MaterialTheme.colorScheme.secondary.copy(0.1F)
        val colors = remember {
            listOf(
                primary,
                Color.Transparent,
                Color.Transparent,
                secondary
            )
        }
        HomePager(
            pagerState = pagerState,
            onNavigateToDetails = onNavigateToDetails,
            onNavigateToSearch = onNavigateToSearch,
            onUpdateUiState = onUpdateUiState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1F)
                .background(brush = Brush.linearGradient(colors = colors))
        )
    }
}
