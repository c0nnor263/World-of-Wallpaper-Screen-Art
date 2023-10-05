package com.notdoppler.feature.store

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.notdoppler.core.ui.theme.WallpapersTheme

@Composable
fun StoreScreen(viewModel: StoreScreenViewModel = hiltViewModel()) {
}

@Preview
@Composable
fun StoreScreenPreview() {
    WallpapersTheme {
        StoreScreen()
    }
}