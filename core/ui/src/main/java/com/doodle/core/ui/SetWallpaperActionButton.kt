package com.doodle.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.doodle.core.domain.enums.ActionType

@Composable
fun SetWallpapersActionButton(isActive: Boolean, onActionClick: (ActionType) -> Unit) {
    var isVisible by remember {
        mutableStateOf(false)
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            Color.Black.copy(0.4F)
        )
    ) {

        AnimatedContent(
            targetState = isVisible,
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
        ) { visible ->

            if (visible) {
                ActionButton(
                    imageVector = ImageVector.vectorResource(R.drawable.set_wallpaper_home_icon),
                    type = ActionType.SET_WALLPAPER,
                    onActionClick = onActionClick,
                    isActive = isActive
                )
            } else {
                FloatingArrowButton(
                    isActive = isActive,
                    onClick = {
                        isVisible = true
                    }
                )
            }
        }
    }
}

@Composable
internal fun FloatingArrowButton(isActive: Boolean, onClick: () -> Unit) {
    val borderWidthAnimation by animateDpAsState(
        targetValue = if (isActive) 4.dp else 0.dp,
        animationSpec = tweenMedium(),
        label = "Border width animation"
    )
    val colorAnimation by animateColorAsState(
        targetValue = if (isActive) Color.White else Color.Gray,
        animationSpec = tweenMedium(),
        label = "Color animation"
    )
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .padding(4.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary),
        border = BorderStroke(
            width = borderWidthAnimation,
            color = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowDropUp,
            contentDescription = null,
            modifier = Modifier
                .size(width = 64.dp, height = 48.dp)
                .clickable(onClick = onClick)
                .padding(4.dp),
            tint = colorAnimation
        )
    }
}
