package com.notdoppler.feature.splash.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.notdoppler.core.ui.theme.WallpapersTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashScreenViewModel,
    onNavigateToHome: () -> Unit,
) {
    SplashScreenContent(onNavigateToHome = onNavigateToHome)

}

@Composable
fun SplashScreenContent(modifier: Modifier = Modifier, onNavigateToHome: () -> Unit) {
    LaunchedEffect(key1 = Unit) {
        delay(3000)
        onNavigateToHome()
    }
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Text(
            stringResource(id = com.notdoppler.core.domain.R.string.app_name),
            style = MaterialTheme.typography.displayLarge
        )

    }
}


@Preview
@Composable
fun SplashScreenContentPreview() {
    WallpapersTheme {
        SplashScreenContent {

        }
    }
}