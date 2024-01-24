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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.doodle.core.domain.enums.RemoveAdsStatus
import com.doodle.core.domain.enums.isNotPurchased
import com.doodle.core.ui.DisposableEffectLifecycle
import com.doodle.core.ui.state.LocalRemoveAdsStatus
import com.doodle.core.ui.theme.WallpapersTheme
import com.doodle.turboracing3.navigation.isPermittedForAppOpenAd
import com.doodle.turboracing3.presentation.composables.AppContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<AppContentViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val backStackEntry = navController.currentBackStackEntryAsState()
            val removeAdsStatus =
                viewModel.isPremiumUser.collectAsStateWithLifecycle(RemoveAdsStatus.NOT_PURCHASED)

            DisposableEffectLifecycle(
                onResume = {
                    viewModel.onResumeBilling()
                    if (removeAdsStatus.value.isNotPurchased() &&
                        backStackEntry.value.isPermittedForAppOpenAd()
                    ) {
                        viewModel.showAppOpenAd(this)
                    }
                }, onDestroy = {
                    viewModel.destroyNativeAds()
                })

            CompositionLocalProvider(LocalRemoveAdsStatus provides removeAdsStatus.value) {
                WallpapersTheme {
                    AppContent(navController)
                }
            }
        }
        setFullscreen()
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
