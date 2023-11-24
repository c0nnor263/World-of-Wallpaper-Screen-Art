package com.notdoppler.feature.home

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import java.net.UnknownHostException

// ACTUAL OFFSET
@OptIn(ExperimentalFoundationApi::class)
fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

fun checkForSpecificException(context: Context, throwable: Throwable?): String {
    return when (throwable) {
        is UnknownHostException -> context.getString(
            R.string.no_internet_connection
        )

        else -> throwable?.localizedMessage ?: ""
    }
}
