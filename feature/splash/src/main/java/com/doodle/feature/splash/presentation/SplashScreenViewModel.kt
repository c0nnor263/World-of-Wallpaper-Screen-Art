package com.doodle.feature.splash.presentation

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import com.doodle.core.advertising.data.AppOpenAdManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val appOpenAdManager: AppOpenAdManager
) : ViewModel() {
    val adStatus = appOpenAdManager.adStatus

    fun showAppOpenAd(activity: ComponentActivity) {
        appOpenAdManager.showAdIfAvailable(activity)
    }
}
