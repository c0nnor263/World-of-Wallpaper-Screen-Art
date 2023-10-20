package com.notdoppler.feature.picturedetails.presentation.common

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.notdoppler.core.domain.enums.ActionType
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.feature.picturedetails.presentation.PictureDetailsScreenContentImage
import com.notdoppler.feature.picturedetails.presentation.common.actions.ActionRow
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailsImage(
    modifier: Modifier = Modifier,
    imageHit: FetchedImage.Hit?,
    onActionClick: (ActionType, FetchedImage.Hit?, Bitmap?) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val image by remember { mutableStateOf(imageHit) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }


    AnchoredDraggableArea(
        modifier = modifier,
        onTopEnd = onNavigateBack,
    ) { draggableInfo ->
        Box {
            AsyncImage(
                image?.largeImageURL,
                onState = { state ->
                    when (state) {
                        is AsyncImagePainter.State.Success -> {
                            bitmap = (state.result.drawable as BitmapDrawable).bitmap
                        }

                        else -> {}
                    }
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
            )


            val isActionRowVisible = remember {
                derivedStateOf {
                    draggableInfo.progress < 0.035F
                }
            }
            ActionRow(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        bottom = 8.dp
                    ),
                visible = isActionRowVisible.value,
                onActionClick = {
                    onActionClick(it, image, bitmap)
                }
            )
        }

    }
}