package com.notdoppler.feature.picturedetails.presentation.common

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.notdoppler.core.domain.enums.ActionType
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.ui.tweenMedium
import com.notdoppler.feature.picturedetails.presentation.PictureDetailsScreenContentImage
import com.notdoppler.feature.picturedetails.presentation.common.actions.ActionRow
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailImage(
    modifier: Modifier = Modifier,
    image: FetchedImage.Hit?,
    isActive: Boolean,
    onActionClick: (ActionType, FetchedImage.Hit?, Bitmap?) -> Unit,
    onNavigateBack: () -> Unit,
    onImageStateChanged: (AsyncImagePainter.State) -> Unit,
) {
    var localBitmap by remember { mutableStateOf<Bitmap?>(null) }
    AnchoredDraggableArea(
        modifier = modifier,
        onTopEnd = onNavigateBack,
    ) { draggableInfo ->
        val isImageSwiping = draggableInfo.progress < 0.035F
        val clipRoundedShapeAnimation by animateDpAsState(
            targetValue = if (isImageSwiping) 0.dp else 24.dp,
            animationSpec = tweenMedium(),
            label = ""
        )

        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                image?.largeImageURL,
                onState = { state ->
                    if (state is AsyncImagePainter.State.Success) {
                        localBitmap = (state.result.drawable as BitmapDrawable).bitmap
                    }
                    onImageStateChanged(state)
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(PictureDetailsScreenContentImage)
                    .scale(1f - draggableInfo.progress)
                    .offset {
                        IntOffset(
                            x = 0,
                            y = draggableInfo.state
                                .requireOffset()
                                .roundToInt()
                        )
                    }
                    .anchoredDraggable(draggableInfo.state, Orientation.Vertical)
                    .clip(
                        RoundedCornerShape(
                            bottomStart = clipRoundedShapeAnimation,
                            bottomEnd = clipRoundedShapeAnimation
                        )
                    )
            )

            ActionRow(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                visible = isImageSwiping,
                userImageUrl = image?.userImageURL ?: "",
                onActionClick = {
                    if (isActive) {
                        onActionClick(it, image, localBitmap)
                    }
                }
            )
        }

    }
}