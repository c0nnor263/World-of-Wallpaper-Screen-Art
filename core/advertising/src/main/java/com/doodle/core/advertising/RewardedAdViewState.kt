package com.doodle.core.advertising

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.doodle.core.advertising.data.AdRetryPolicy
import com.doodle.core.advertising.enums.RewardedAdResult
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun rememberRewardedAdViewState(
    activity: ComponentActivity,
    adUnitID: String = stringResource(id = com.doodle.core.data.R.string.admob_rewarded_ad_unit_id)
): RewardedAdViewState {
    val scope = rememberCoroutineScope()
    return remember {
        RewardedAdViewState(
            adUnitID = adUnitID,
            scope = scope
        ).also { state ->
            state.loadOrGetAd(activity)
        }
    }
}

class RewardedAdViewState(
    private val scope: CoroutineScope,
    private val adUnitID: String
) {
    private val retryPolicy = AdRetryPolicy()
    var rewardedAd by mutableStateOf<RewardedAd?>(null)
        private set

    fun loadOrGetAd(activity: ComponentActivity) {
        scope.launch {
            RewardedAdManager
                .loadAd(activity, adUnitID)
                .fold(
                    onSuccess = { ad ->
                        rewardedAd = ad
                        retryPolicy.reset()
                        Log.i("TAG", "loadOrGetAd: onSuccess")
                    },
                    onFailure = {
                        retryPolicy.retry {
                            loadOrGetAd(activity)
                        }
                        Log.i("TAG", "loadOrGetAd: onFailure: $it")
                    }
                )
        }
    }

    fun showAd(
        activity: ComponentActivity,
        onDismissed: (result: RewardedAdResult) -> Unit = {}
    ) {
        rewardedAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    rewardedAd = null
                    RewardedAdManager.reset()
                    loadOrGetAd(activity)
                    onDismissed(RewardedAdResult.DISMISSED)
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    super.onAdFailedToShowFullScreenContent(error)
                    rewardedAd = null
                    RewardedAdManager.reset()
                    loadOrGetAd(activity)
                    onDismissed(RewardedAdResult.ERROR)
                }
            }

        rewardedAd?.show(activity) {
            onDismissed(RewardedAdResult.REWARDED)
        } ?: onDismissed(RewardedAdResult.ERROR)
    }
}

object RewardedAdManager {

    private var lastLoadedAd: RewardedAd? = null
    private var lastLoadTime: Long = 0
    private val isAdAvailable
        get() = lastLoadedAd != null

    suspend fun loadAd(
        context: Context,
        adUnitID: String
    ) =
        suspendCoroutine { continuation ->
            if (wasLoadTimeLessThanLimitHoursAgo(lastLoadTime, 1) && isAdAvailable) {
                continuation.resume(Result.success(lastLoadedAd))
                return@suspendCoroutine
            }

            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                adUnitID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        super.onAdLoaded(ad)
                        lastLoadTime = System.currentTimeMillis()
                        lastLoadedAd = ad
                        continuation.resume(Result.success(ad))
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        super.onAdFailedToLoad(error)
                        val adException = Exception(error.message)
                        val result = Result.failure<RewardedAd>(adException)
                        continuation.resume(result)
                    }
                }
            )
        }

    fun reset() {
        lastLoadedAd = null
    }
}
