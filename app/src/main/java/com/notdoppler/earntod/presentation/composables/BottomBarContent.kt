package com.notdoppler.earntod.presentation.composables

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.notdoppler.core.data.R

@Composable
fun BottomBarContent(modifier: Modifier = Modifier) {
    AnimatedVisibility(
        modifier = modifier,
        visible = true
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            BannerAdView(
                modifier = Modifier.fillMaxWidth(),
                adUnitId = stringResource(id = R.string.admob_banner_ad_unit_id)
            )
        }
    }
}



@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String
) {
    val deviceCurrentWidth = LocalConfiguration.current.screenWidthDp
    AndroidView(modifier = modifier, factory = { context: Context ->
        AdView(context).apply {
            setAdSize(
                AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    context, deviceCurrentWidth
                )
            )
            setAdUnitId(adUnitId)
            loadAd(AdRequest.Builder().build())
        }
    }, update = { adView ->
        adView.loadAd(AdRequest.Builder().build())
    })
}