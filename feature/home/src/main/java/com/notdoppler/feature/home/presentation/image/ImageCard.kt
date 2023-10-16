package com.notdoppler.feature.home.presentation.image

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.notdoppler.core.domain.model.remote.FetchedImage

@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    image: FetchedImage.Hit,
    onNavigateToDetails: (FetchedImage.Hit) -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = { onNavigateToDetails(image) }),
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .defaultMinSize(minHeight = 200.dp),
            model = image.previewURL,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}