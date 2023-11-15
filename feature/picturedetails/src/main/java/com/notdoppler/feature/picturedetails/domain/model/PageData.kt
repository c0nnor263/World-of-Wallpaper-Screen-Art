package com.notdoppler.feature.picturedetails.domain.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.ads.nativead.NativeAd
import com.notdoppler.core.domain.model.remote.FetchedImage

data class PageData(
    val image: MutableState<FetchedImage.Hit?> = mutableStateOf(null),
    val nativeAd: MutableState<NativeAd?> = mutableStateOf(null),
)
