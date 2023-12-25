package com.doodle.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.doodle.core.ui.card.CardImage

@Composable
fun FetchedImageItem(
    modifier: Modifier = Modifier,
    previewURL: String,
    onNavigateToDetails: () -> Unit
) {
    CardImage(modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .defaultMinSize(minHeight = 200.dp)
                .clickable(onClick = onNavigateToDetails),
            model = previewURL,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}
