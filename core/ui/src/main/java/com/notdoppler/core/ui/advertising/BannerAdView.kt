package com.notdoppler.core.ui.advertising

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

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

@Preview
@Composable
fun BannerAdViewPreview() {
    BannerAdView(adUnitId = "")
}