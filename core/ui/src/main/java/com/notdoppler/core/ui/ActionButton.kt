package com.notdoppler.core.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.notdoppler.core.domain.enums.ActionType

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    @DrawableRes id: Int,
    type: ActionType = ActionType.NOT_DEFINED,
    isActive: Boolean,
    color: Color = Color.White,
    onActionClick: (ActionType) -> Unit
) {
    val borderWidthAnimation by animateDpAsState(
        targetValue = if (isActive) 4.dp else 0.dp,
        animationSpec = tweenMedium(),
        label = "Border width animation"
    )
    val colorAnimation by animateColorAsState(
        targetValue = if (isActive) color else Color.Gray,
        animationSpec = tweenMedium(),
        label = "Color animation"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed.value) 0.9F else 1F,
        label = ""
    )
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = modifier
            .padding(8.dp)
            .scale(buttonScale),
        shape = CircleShape,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary),
        border = BorderStroke(
            width = borderWidthAnimation,
            color = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        IconButton(
            onClick = {},
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                ImageVector.vectorResource(id = id),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        enabled = isActive,
                        interactionSource = interactionSource,
                        indication = LocalIndication.current
                    ) {
                        onActionClick(type)
                    }
                    .padding(16.dp),
                tint = colorAnimation
            )
        }
    }
}

@Composable
fun ActionButtonWithImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    type: ActionType,
    isActive: Boolean,
    onActionClick: (ActionType) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed.value) 0.9F else 1F,
        label = ""
    )
    Card(
        elevation = CardDefaults.cardElevation(12.dp),
        modifier = modifier
            .padding(8.dp)
            .scale(buttonScale),
        shape = RoundedCornerShape(48.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .clickable(
                    enabled = isActive,
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    onActionClick(type)
                }
        )
    }
}
