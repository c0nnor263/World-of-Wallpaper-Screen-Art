package com.notdoppler.feature.home.presentation.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notdoppler.feature.home.domain.tabInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
 fun TabImages(modifier: Modifier = Modifier, pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    ScrollableTabRow(
        modifier = modifier,
        selectedTabIndex = pagerState.currentPage,
        edgePadding = 0.dp,
    ) {
        tabInfo.forEachIndexed { index, tabInfo ->
            Tab(
                text = { Text(text = tabInfo.title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}
