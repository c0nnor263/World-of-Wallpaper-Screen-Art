package com.notdoppler.earntod.presentation

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.notdoppler.core.advertising.data.AppOpenAdManager
import com.notdoppler.core.ui.theme.WallpapersTheme
import com.notdoppler.earntod.presentation.composables.AppContent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var appOpenAdManager: AppOpenAdManager


    private val viewModel by viewModels<AppContentViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            ObjectAnimator.ofFloat(
                splashScreenView.view,
                View.TRANSLATION_Z,
                0f,
                1F
            ).apply {
                interpolator = LinearInterpolator()
                duration = 500L
                doOnEnd { splashScreenView.remove() }
                start()
            }
        }
        super.onCreate(savedInstanceState)
        setContent {
            WallpapersTheme {
                AppContent()
            }
        }


        setFullscreen()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResumeBilling()
        appOpenAdManager.showAdIfAvailable(this)
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