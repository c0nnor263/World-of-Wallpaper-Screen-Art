package com.notdoppler.feature.picturedetails.presentation.common

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.notdoppler.feature.picturedetails.domain.enums.DragAnchors
import com.notdoppler.feature.picturedetails.domain.model.AnchoredDraggableInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnchoredDraggableArea(
    modifier: Modifier = Modifier,
    onTopEnd: () -> Unit,
    content: @Composable (AnchoredDraggableInfo) -> Unit
) {
    val density = LocalDensity.current
    var isTopEnd by remember { mutableStateOf(false) }
    var lastProgress: Float by remember { mutableFloatStateOf(0F) }

    val state = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Start,
            positionalThreshold = { distance: Float -> distance * 1f },
            velocityThreshold = { with(density) { 1000.dp.toPx() } },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        )
        { anchor ->
            when (anchor) {
                DragAnchors.TopEnd -> {
                    if (!isTopEnd) onTopEnd()
                    isTopEnd = true
                    true
                }

                else -> {
                    lastProgress = 0F
                    true
                }
            }

        }
    }.apply {
        progress.takeUnless { it == 1.0F }?.times(0.1f)?.also {
            lastProgress = it
        }
    }

    Box(
        modifier
            .onSizeChanged { layoutSize ->
                state.updateAnchors(
                    DraggableAnchors {
                        DragAnchors.entries.forEach { anchor ->
                            anchor at layoutSize.height * anchor.fraction
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        content(
            AnchoredDraggableInfo(
                state = state,
                progress = lastProgress,
                isEnd = isTopEnd
            )
        )
    }
}