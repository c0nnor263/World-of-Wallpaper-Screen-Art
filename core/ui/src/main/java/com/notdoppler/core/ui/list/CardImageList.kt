package com.notdoppler.core.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notdoppler.core.ui.ActionButton
import com.notdoppler.core.ui.R
import com.notdoppler.core.ui.tweenMedium
import kotlinx.coroutines.launch

@Composable
fun CardImageList(
    modifier: Modifier = Modifier,
    columns: StaggeredGridCells,
    isItemsEmpty: Boolean,
    onEmptyContent: @Composable () -> Unit = {},
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    content: LazyStaggeredGridScope.() -> Unit
) {
    if (isItemsEmpty) {
        onEmptyContent()
    } else {
        val scope = rememberCoroutineScope()
        val showUpButton by remember {
            derivedStateOf {
                state.firstVisibleItemIndex > 0
            }
        }

        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            LazyVerticalStaggeredGrid(
                state = state,
                columns = columns,
                contentPadding = PaddingValues(2.dp, 4.dp, 2.dp, 2.dp),
                verticalItemSpacing = 4.dp,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                content()
            }

            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                visible = showUpButton,
                enter = fadeIn(tweenMedium()) + scaleIn(tweenMedium()),
                exit = scaleOut(tweenMedium()) + fadeOut(tweenMedium())
            ) {
                ActionButton(
                    id = R.drawable.baseline_arrow_upward_24,
                    isActive = showUpButton,
                    onActionClick = { _ ->
                        scope.launch {
                            state.animateScrollToItem(0)
                        }
                    }
                )
            }
        }
    }
}
