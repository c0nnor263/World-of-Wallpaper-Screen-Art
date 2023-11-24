package com.notdoppler.feature.picturedetails.presentation.common.actions

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notdoppler.core.domain.enums.ActionType
import com.notdoppler.core.ui.ActionButton
import com.notdoppler.core.ui.ActionButtonWithImage
import com.notdoppler.core.ui.R
import com.notdoppler.core.ui.theme.WallpapersTheme
import com.notdoppler.core.ui.tweenMedium
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
    isActive: Boolean,
    userImageUrl: String,
    onActionClick: (ActionType) -> Unit
) {
    val isFavoriteIconEnabled = LocalFavoriteIconEnabled.current

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = slideInVertically(tweenMedium()) { it } + scaleIn(tweenMedium()),
        exit = scaleOut(tweenMedium()) + slideOutVertically(tweenMedium()) { it }
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                MaterialTheme.colorScheme.surface.copy(0.6F)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                ActionButton(
                    id = if (isFavoriteIconEnabled) {
                        R.drawable.baseline_favorite_24
                    } else {
                        R.drawable.baseline_favorite_border_24
                    },
                    type = ActionType.FAVORITE,
                    onActionClick = onActionClick,
                    color = if (isFavoriteIconEnabled) Color.Red else Color.White,
                    isActive = isActive,
                    modifier = Modifier.testTag(PictureDetailsScreenContentFavoriteTag)
                )
                ActionButton(
                    id = R.drawable.baseline_download_24,
                    type = ActionType.DOWNLOAD,
                    onActionClick = onActionClick,
                    isActive = isActive,
                    modifier = Modifier.testTag(PictureDetailsScreenContentDownloadTag)
                )
                ActionButton(
                    id = R.drawable.baseline_share_24,
                    type = ActionType.SHARE,
                    onActionClick = onActionClick,
                    isActive = isActive,
                    modifier = Modifier.testTag(PictureDetailsScreenContentShareTag)
                )
                ActionButtonWithImage(
                    imageUrl = userImageUrl,
                    type = ActionType.PUBLISHER_INFO,
                    onActionClick = onActionClick,
                    isActive = isActive,
                    modifier = Modifier.testTag(PictureDetailsScreenContentPublisherInfoTag)
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ActionRowPreview() {
    WallpapersTheme {

        ActionRow(
            visible = true,
            isActive = true,
            userImageUrl = "",
            onActionClick = {}
        )
    }
}
