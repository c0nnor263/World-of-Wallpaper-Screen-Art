package com.doodle.turboracing3.presentation

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doodle.core.domain.enums.RemoveAdsStatus
import com.doodle.core.ui.state.LocalRemoveAdsStatus
import com.doodle.core.ui.theme.WallpapersTheme
import com.doodle.turboracing3.presentation.composables.AppContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<AppContentViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val removeAdsStatus =
                viewModel.isPremiumUser.collectAsStateWithLifecycle(RemoveAdsStatus.NOT_PURCHASED)

            CompositionLocalProvider(LocalRemoveAdsStatus provides removeAdsStatus.value) {
                WallpapersTheme {
                    AppContent()
                }
            }
        }
        setFullscreen()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResumeBilling()
    }

    private fun setFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.insetsController?.apply {
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                hide(WindowInsets.Type.systemBars())
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setDisplayCutoutMode()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setDisplayCutoutMode() {
        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
}
