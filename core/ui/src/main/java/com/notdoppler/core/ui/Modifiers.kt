package com.notdoppler.core.ui

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

@SuppressLint("ComposableModifierFactory")
@Composable
fun Modifier.scaleWithPressAnimation(
    isPressed: Boolean,
    from: Float = 1F,
    to: Float = 0.6F
): Modifier {
    val scaleAnimation by animateFloatAsState(
        targetValue = if (isPressed) to else from,
        animationSpec = tweenEasy(),
        label = "Default press scale animation"
    )
    return this then Modifier.scale(scaleAnimation)
}
