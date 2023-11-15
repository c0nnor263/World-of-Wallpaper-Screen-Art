package com.notdoppler.core.advertising.data

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.notdoppler.core.advertising.wasLoadTimeLessThanLimitHoursAgo
import com.notdoppler.core.data.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NativeAdManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        private const val RANGE_FREQUENCY = 8
    }

    private val nativeAds = mutableListOf<NativeAd>()
    private var loadTime: Long = 0
    private var isActivityDestroyed = false

    init {
        loadAds()
    }

    fun loadAds() {
        val adLoader = AdLoader.Builder(
            context,
            context.getString(R.string.admob_native_ad_unit_id)
        ).forNativeAd { nativeAd ->
            if (isActivityDestroyed) {
                nativeAd.destroy()
                return@forNativeAd
            }

            nativeAds.add(nativeAd)
        }.withAdListener(
            object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    Log.i("TAG", "onAdFailedToLoad: ${error.message} ${error.cause}")
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    loadTime = System.currentTimeMillis()
                }
            }
        ).withNativeAdOptions(
            NativeAdOptions.Builder()
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_LEFT)
                .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_PORTRAIT)
                .build()
        )
            .build()

        adLoader.loadAds(AdRequest.Builder().build(), 5)
    }

    fun getNativeAdById(id: Int): NativeAd? {
        if (isAdsExpired()) {
            loadAds()
            return null
        }

        val isAd = id % RANGE_FREQUENCY == 0
        return if (isAd && nativeAds.isNotEmpty()) {
            val index = (id / RANGE_FREQUENCY) % nativeAds.size
            nativeAds[index]
        } else {
            null
        }
    }

    fun isAdsExpired(): Boolean {
        return wasLoadTimeLessThanLimitHoursAgo(loadTime, 1)
    }


    fun onActivityDestroy() {
        isActivityDestroyed = true
        nativeAds.forEach {
            it.destroy()
        }
    }
}