package com.notdoppler.feature.home.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.notdoppler.core.ui.NoRippleInteractionSource
import com.notdoppler.core.ui.theme.WallpapersTheme
import com.notdoppler.core.ui.tweenEasy
import com.notdoppler.core.ui.tweenMedium
import com.notdoppler.feature.home.domain.enums.TabPage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabImages(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    isLoading: Boolean,
) {
    val scope = rememberCoroutineScope()
    val loadingBarHeightAnimation by animateDpAsState(
        targetValue = if (isLoading) 2.dp else 6.dp,
        animationSpec = tweenMedium(),
        label = "Loading bar height animation"
    )


    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        ScrollableTabRow(
            modifier = modifier.fillMaxWidth(),
            selectedTabIndex = pagerState.currentPage,
            edgePadding = maxWidth - minWidth * 0.8F,
            indicator = {
                TabIndicator(
                    tabPositions = it,
                    tabPage = TabPage.entries[pagerState.currentPage],
                    isLoading = isLoading
                )
            },
            divider = {
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(loadingBarHeightAnimation),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(loadingBarHeightAnimation)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            },
        ) {
            TabPage.entries.forEachIndexed { index, page ->
                val selected = pagerState.currentPage == index
                val interactionSource = remember { NoRippleInteractionSource() }
                Tab(
                    text = {
                        AnimatedVisibility(
                            visible = !isLoading || !selected,
                            enter = fadeIn(tweenMedium()),
                            exit = fadeOut(tweenMedium()),
                        ) {
                            Text(
                                text = page.name,
                                modifier = Modifier.padding(4.dp),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    interactionSource = interactionSource,
                )
            }
        }
    }
}


@Composable
fun TabIndicator(
    tabPositions: List<TabPosition>,
    tabPage: TabPage,
    isLoading: Boolean,
) {
    val transition = updateTransition(
        tabPage, label = "Tab indicator"
    )
    val indicatorLeft by transition.animateDp(
        transitionSpec = {
            tweenMedium(delayMillis = 200, easing = LinearOutSlowInEasing)
        }, label = "Indicator left"
    ) { page ->
        tabPositions[page.ordinal].left
    }
    val indicatorRight by transition.animateDp(
        transitionSpec = {
            tweenMedium(delayMillis = 200, easing = LinearOutSlowInEasing)
        }, label = "Indicator right"
    ) { page ->
        tabPositions[page.ordinal].right
    }
    val scaleAnimation by animateFloatAsState(
        targetValue = if (transition.isRunning) 0.6F else 1F,
        animationSpec = tweenEasy(easing = FastOutSlowInEasing),
        label = "Scale animation"
    )

    val isTransiting = transition.isRunning || isLoading

    val borderAnimation by animateDpAsState(
        targetValue = if (isTransiting) 0.dp else 2.dp,
        animationSpec = tweenEasy(),
        label = "Border animation"
    )

    val bottomIndicatorHeight by animateDpAsState(
        targetValue = if (isTransiting) 0.dp else 4.dp,
        animationSpec = tweenEasy(),
        label = "Bottom indicator height"
    )

    Box(
        modifier = Modifier
            .zIndex(-1F)
            .fillMaxSize()
            .wrapContentSize(align = Alignment.BottomStart)
            .offset(x = indicatorLeft)
            .width(indicatorRight - indicatorLeft)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .scale(scaleAnimation),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomEnd = if (isTransiting) 16.dp else 0.dp,
                bottomStart = if (isTransiting) 16.dp else 0.dp
            ),
            border = BorderStroke(borderAnimation, MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = isLoading
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                }
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(bottomIndicatorHeight)
                .align(Alignment.BottomCenter)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun TabImagesPreview() {
    WallpapersTheme {
        val pagerState = rememberPagerState(initialPage = TabPage.LATEST.ordinal) {
            TabPage.entries.size
        }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(key1 = Unit) {
            delay(5000)
            isLoading = false
        }
        TabImages(
            pagerState = pagerState, isLoading = isLoading
        )
    }
}