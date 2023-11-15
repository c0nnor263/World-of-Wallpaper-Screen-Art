package com.notdoppler.feature.splash.presentation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notdoppler.core.advertising.domain.enums.AdStatus
import com.notdoppler.core.ui.theme.WallpapersTheme

@Composable
fun SplashScreen(
    viewModel: SplashScreenViewModel,
    onNavigateToHome: () -> Unit,
) {
    val context = LocalContext.current
    val adStatus = viewModel.adStatus.collectAsStateWithLifecycle()
    LaunchedEffect(adStatus.value) {
        when (adStatus.value) {
            AdStatus.LOADING -> {}
            else -> {
                val activity = context as ComponentActivity
                viewModel.showAppOpenAd(activity)
                onNavigateToHome()
            }
        }
    }
    SplashScreenContent()

}

@Composable
fun SplashScreenContent(modifier: Modifier = Modifier) {
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
        SplashScreenContent()
    }
}