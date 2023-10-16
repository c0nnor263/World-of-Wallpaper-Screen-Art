package com.notdoppler.feature.picturedetails.presentation.common.actions

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.notdoppler.core.domain.enums.ActionType
import com.notdoppler.core.ui.R
import com.notdoppler.feature.picturedetails.state.LocalFavoriteIconEnabled

const val PictureDetailsScreenContentShareTag = "PictureDetailsScreenContentShareTag"
const val PictureDetailsScreenContentFavoriteTag = "PictureDetailsScreenContentFavoriteTag"
const val PictureDetailsScreenContentDownloadTag = "PictureDetailsScreenContentDownloadTag"
const val PictureDetailsScreenContentPublisherInfoTag =
    "PictureDetailsScreenContentPublisherInfoTag"

@Composable
fun ActionRow(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onActionClick: (ActionType) -> Unit,
) {
    val isFavoriteIconEnabled = LocalFavoriteIconEnabled.current
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = slideInVertically(tween(500)) { it } + scaleIn(tween(500)),
        exit = scaleOut(tween(500)) + slideOutVertically(tween(500)) { it }
    ) {
        Row(modifier = modifier) {
            ActionButton(
                id = if (isFavoriteIconEnabled) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24,
                type = ActionType.FAVORITE,
                onActionClick = onActionClick,
                color = if (isFavoriteIconEnabled) Color.Red else Color.White,
                modifier = Modifier.testTag(PictureDetailsScreenContentFavoriteTag)
            )
            ActionButton(
                id = R.drawable.baseline_download_24,
                type = ActionType.DOWNLOAD,
                onActionClick = onActionClick,
                modifier = Modifier.testTag(PictureDetailsScreenContentDownloadTag)
            )
            ActionButton(
                id = R.drawable.baseline_share_24,
                type = ActionType.SHARE,
                onActionClick = onActionClick,
                modifier = Modifier.testTag(PictureDetailsScreenContentShareTag)
            )
            ActionButton(
                id = R.drawable.baseline_account_circle_24,
                type = ActionType.PUBLISHER_INFO,
                onActionClick = onActionClick,
                modifier = Modifier.testTag(PictureDetailsScreenContentPublisherInfoTag)
            )
        }
    }
}


@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    @DrawableRes id: Int,
    type: ActionType,
    color: Color = Color.White,
    onActionClick: (ActionType) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed.value) 0.9F else 1F, label = ""
    )
    Card(
        elevation = CardDefaults.cardElevation(12.dp),
        modifier = modifier
            .padding(8.dp)
            .scale(buttonScale),
        shape = RoundedCornerShape(48.dp)
    ) {
        IconButton(
            onClick = { onActionClick(type) },
            interactionSource = interactionSource,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                ImageVector.vectorResource(id = id),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = color
            )
        }
    }
}
