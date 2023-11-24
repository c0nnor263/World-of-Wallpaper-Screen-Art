package com.notdoppler.core.advertising.data

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.notdoppler.core.advertising.domain.DEFAULT_LOAD_RETRY
import com.notdoppler.core.advertising.domain.enums.AdStatus
import com.notdoppler.core.advertising.wasLoadTimeLessThanLimitHoursAgo
import com.notdoppler.core.data.R
import com.notdoppler.core.domain.di.ApplicationScope
import com.notdoppler.core.domain.source.local.repository.AppPreferencesDataStoreRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppOpenAdManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val appPreferencesDataStoreRepository: AppPreferencesDataStoreRepository
) {
    private val _adStatus = MutableStateFlow(AdStatus.LOADING)
    val adStatus: StateFlow<AdStatus> = _adStatus

    private var appOpenAd: AppOpenAd? = null
    private var loadRetry = DEFAULT_LOAD_RETRY
    private var loadTime = 0L
    private var isLoadingAd = false
    private var isShowingAd = false

    init {
        loadAd()
    }

    private fun loadAd() = applicationScope.launch(Dispatchers.Main) {
        if (isLoadingAd || isAdAvailable()) {
            return@launch
        }
        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            context.getString(R.string.admob_app_open_ad_unit_id),
            request,
            object : AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    super.onAdLoaded(ad)
                    updateAppOpenAd(ad, AdStatus.LOADED)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    updateAppOpenAd(null, AdStatus.FAILED)
                }
            }
        )
    }

    fun showAdIfAvailable(activity: Activity) = applicationScope.launch(Dispatchers.Main) {
        if (isShowingAd) {
            return@launch
        }
        if (!isAdAvailable()) {
            loadAd()
            return@launch
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                updateAppOpenAd(null, AdStatus.DISSMISSED)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                updateAppOpenAd(null, AdStatus.FAILED_TO_SHOW)
            }
        }
        isShowingAd = true
        appOpenAd?.show(activity)
    }

    private suspend fun isAdAvailable(): Boolean {
        return appOpenAd != null &&
            !wasLoadTimeLessThanLimitHoursAgo(loadTime, 4) &&
            appPreferencesDataStoreRepository.getIsAvailableForAppOpenAd()
    }

    private fun updateAppOpenAd(ad: AppOpenAd?, adStatus: AdStatus) {
        when (adStatus) {
            AdStatus.LOADED -> {
                appOpenAd = ad
                isLoadingAd = false
                loadRetry = DEFAULT_LOAD_RETRY
                loadTime = System.currentTimeMillis()
            }

            AdStatus.FAILED_TO_SHOW, AdStatus.DISSMISSED -> {
                appOpenAd = ad
                isShowingAd = false
                loadAd()
            }

            AdStatus.FAILED -> {
                isLoadingAd = false
                if (loadRetry > 0) {
                    loadRetry--
                    loadAd()
                }
            }

            else -> {}
        }
        _adStatus.value = adStatus
    }
}
