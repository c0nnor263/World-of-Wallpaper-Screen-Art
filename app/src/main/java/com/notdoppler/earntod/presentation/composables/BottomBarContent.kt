package com.notdoppler.earntod.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.notdoppler.core.data.R
import com.notdoppler.core.ui.advertising.BannerAdView

@Composable
fun BottomBarContent(modifier: Modifier = Modifier) {
    AnimatedVisibility(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
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


@Preview
@Composable
fun WordeBottomAppBarPreview() {
    BottomBarContent()
}