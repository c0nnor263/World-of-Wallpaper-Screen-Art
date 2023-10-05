package com.notdoppler.feature.settings

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.notdoppler.core.ui.theme.WallpapersTheme


@Composable
fun SettingsScreen(viewModel: SettingsScreenViewModel = hiltViewModel()) {

}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun SettingsScreenPreview() {
    WallpapersTheme {
        Surface {
            SettingsScreen()
        }
    }
}