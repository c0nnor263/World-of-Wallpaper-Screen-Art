package com.doodle.feature.picturedetails.state

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.doodle.core.domain.model.remote.RemoteImage
import com.doodle.feature.picturedetails.domain.model.PageData
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberPagerDetailState(
    initialPage: Int,
    imagesState: Flow<PagingData<RemoteImage.Hit>>,
    onGetNativeAd: (Int) -> NativeAd?,
    onDismissAd: (Int) -> Unit,
    onCheckFavorite: (Int?) -> Unit
): PagerPictureDetailState {
    val images = imagesState.collectAsLazyPagingItems()
    val pagerState = rememberPagerState(
        initialPage = initialPage
    ) { images.itemCount }

    LaunchedEffect(pagerState.currentPage, Unit, images.itemCount) {
        if (images.itemCount > 0) {
            val imageId = images[pagerState.currentPage]?.id
            onCheckFavorite(imageId)
        }
    }

    return remember {
        PagerPictureDetailState(
            pagerState = pagerState,
            images = images,
            onGetNativeAd = onGetNativeAd,
            onDismissNativeAd = onDismissAd
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
class PagerPictureDetailState(
    val pagerState: PagerState,
    private val images: LazyPagingItems<RemoteImage.Hit>,
    private val onGetNativeAd: (Int) -> NativeAd?,
    private val onDismissNativeAd: (Int) -> Unit
) {
    fun getKey(id: Int): Int {
        return images[id]?.id ?: 0
    }

    fun getPageData(id: Int): PageData {
        return PageData(
            image = mutableStateOf(images[id]),
            nativeAd = mutableStateOf(
                onGetNativeAd(id)
            )
        )
    }

    fun onDismissAd(id: Int) {
        onDismissNativeAd(id)
    }

    fun isActiveNow(pageIndex: Int): Boolean {
        val settledPage = pagerState.settledPage
        val limitToCurrentPage = pagerState.currentPage + 1
        return settledPage.coerceAtMost(limitToCurrentPage) == pageIndex
    }
}
