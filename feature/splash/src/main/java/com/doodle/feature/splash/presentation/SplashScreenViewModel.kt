package com.doodle.feature.splash.presentation

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doodle.core.advertising.data.AppOpenAdManager
import com.doodle.core.domain.di.IoDispatcher
import com.doodle.core.domain.source.local.repository.AppPreferencesDataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val appPreferencesDataStoreRepository: AppPreferencesDataStoreRepository,
    private val appOpenAdManager: AppOpenAdManager
) : ViewModel() {
    val appOpenAdStatus = appOpenAdManager.adStatus

    fun showAppOpenAd(activity: ComponentActivity) {
        appOpenAdManager.showAdIfAvailable(activity)
    }

    fun incrementAppOpenTimes() = viewModelScope.launch(ioDispatcher) {
        appPreferencesDataStoreRepository.incrementAppOpenTimes()
    }
}
