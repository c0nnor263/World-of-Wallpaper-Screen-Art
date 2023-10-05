package com.notdoppler.feature.picturedetails

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.notdoppler.core.domain.domain.model.FetchedImage
import com.notdoppler.core.ui.theme.WallpapersTheme


@Composable
fun PictureDetailsScreen(
    viewModel: PictureDetailsViewModel = hiltViewModel(),
    imageHit: FetchedImage.Hit?,
    onNavigateBack: () -> Unit
) {
    AsyncImage(
        imageHit?.largeImageURL,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}

