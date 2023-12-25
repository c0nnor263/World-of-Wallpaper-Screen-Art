package com.doodle.feature.picturedetails.domain.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.doodle.core.domain.model.remote.RemoteImage
import com.google.android.gms.ads.nativead.NativeAd

data class PageData(
    val image: MutableState<RemoteImage.Hit?> = mutableStateOf(null),
    val nativeAd: MutableState<NativeAd?> = mutableStateOf(null)
)
