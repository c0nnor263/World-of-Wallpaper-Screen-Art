package com.doodle.feature.home.presentation.common

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.doodle.core.billing.domain.enums.BuyProductResult
import com.doodle.core.ui.scaleWithPressAnimation
import com.doodle.core.ui.showShareDialog
import com.doodle.core.ui.theme.WallpapersTheme
import com.doodle.core.ui.tweenMedium
import com.doodle.feature.home.R
import kotlinx.coroutines.launch

@Composable
fun HomeNavigationDrawer(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    onRestorePurchases: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onShowReview: () -> Unit,
    onRequestRemoveAds: () -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeModalNavigationDrawerContent(
                drawerState = drawerState,
                onShowReview = {
                    scope.launch {
                        onShowReview()
                        drawerState.close()
                    }
                },
                onRequestRemoveAds = {
                    scope.launch {
                        onRequestRemoveAds()
                        drawerState.close()
                    }
                },
                onRestorePurchases = {
                    scope.launch {
                        onRestorePurchases()
                        drawerState.close()
                    }
                },
                onNavigateToFavorites = onNavigateToFavorites
            )
        },
        gesturesEnabled = drawerState.isOpen,
        scrimColor = Color.Transparent,
        modifier = modifier
    ) {}
}

@Composable
private fun HomeModalNavigationDrawerContent(
    drawerState: DrawerState,
    onShowReview: () -> Unit,
    onRestorePurchases: () -> Unit,
    onRequestRemoveAds: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ModalDrawerSheet(
        modifier = Modifier
            .wrapContentWidth(Alignment.Start)
            .fillMaxWidth(0.7F),
        drawerContainerColor = Color.Transparent
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1F),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
                contentPadding = PaddingValues(8.dp)
            ) {
                item {
                    HomeNavigationDrawerItem(
                        label = R.string.favorites_wallpapers,
                        imageVector = Icons.Default.Favorite,
                        color = Color(0xFFF08080),
                        onClick = {
                            scope.launch {
                                onNavigateToFavorites()
                                drawerState.close()
                            }
                        }
                    )
                }
                item {
                    HomeNavigationDrawerItem(
                        label = R.string.remove_ads,
                        imageVector = ImageVector.vectorResource(
                            com.doodle.core.ui.R.drawable.remove_ads_icon
                        ),
                        color = Color(0xFFF0E68C),
                        onClick = onRequestRemoveAds
                    )
                }
                item {
                    HomeNavigationDrawerItem(
                        label = R.string.privacy_policy,
                        imageVector = Icons.Default.PrivacyTip,
                        color = Color(0xFF00BFFF),
                        onClick = {
                            val policyUrl = context.getString(R.string.privacy_policy_link)
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(policyUrl)
                            )
                            context.startActivity(intent)
                        }
                    )
                }
                item {
                    HomeNavigationDrawerItem(
                        label = R.string.rate_us,
                        imageVector = Icons.Default.StarRate,
                        color = Color(0xFF00FA9A),
                        onClick = onShowReview
                    )
                }
                item {
                    HomeNavigationDrawerItem(
                        label = R.string.share_app,
                        imageVector = Icons.Default.Share,
                        color = Color(0xFFFFE4B5),
                        onClick = {
                            showShareDialog(context)
                        }
                    )
                }
                item {
                    HomeNavigationDrawerItem(
                        label = R.string.contact_us,
                        imageVector = Icons.Default.ContactSupport,
                        color = Color(0xFFADD8E6),
                        onClick = {
                            Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:")
                                putExtra(
                                    Intent.EXTRA_EMAIL,
                                    context.getString(R.string.developer_email)
                                )
                                val intent = Intent.createChooser(
                                    this,
                                    context.getString(R.string.contact_us)
                                )
                                context.startActivity(intent)
                            }
                        }
                    )
                }
            }
            TextButton(onClick = onRestorePurchases) {
                Text(
                    text = stringResource(com.doodle.core.ui.R.string.restore_purchases),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

val LABEL_HEIGHT = 40.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeNavigationDrawerItem(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    color: Color = MaterialTheme.colorScheme.surface,
    imageVector: ImageVector,
    onClick: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var isShowLabel by remember { mutableStateOf(false) }

    val bottomPadding by animateDpAsState(
        targetValue = if (isShowLabel) LABEL_HEIGHT - 14.dp else 0.dp,
        animationSpec = tweenMedium(), label = ""
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(8.dp)
            .scaleWithPressAnimation(isPressed, from = 1f, to = 0.95F),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isShowLabel,
            enter = slideInVertically(tweenMedium()) { -it },
            exit = slideOutVertically(tweenMedium()) { -it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(LABEL_HEIGHT)
        ) {
            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = label),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 4.dp)
                        .padding(top = (LABEL_HEIGHT - 10.dp) / 2)
                )
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                color
            ),
            modifier = Modifier.padding(bottom = bottomPadding)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .combinedClickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                        onLongClick = {
                            isShowLabel = !isShowLabel
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Black.copy(0.8F)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeModalNavigationDrawerContentPreview() {
    WallpapersTheme {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
        HomeModalNavigationDrawerContent(
            drawerState = drawerState,
            onShowReview = {},
            onRequestRemoveAds = { BuyProductResult.SUCCESS },
            onNavigateToFavorites = {},
            onRestorePurchases = {}
        )
    }
}
