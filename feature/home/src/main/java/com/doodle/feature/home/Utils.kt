package com.doodle.feature.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import java.net.UnknownHostException

@OptIn(ExperimentalFoundationApi::class)
fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

fun checkForSpecificException(context: Context, throwable: Throwable?): String {
    Log.i("TAG", "checkForSpecificException: ${throwable?.stackTraceToString()}")
    return when (throwable) {
        is UnknownHostException -> context.getString(
            R.string.no_internet_connection
        )

        else -> throwable?.localizedMessage ?: ""
    }
}

@Composable
fun PagingLaunchedEffect(
    states: CombinedLoadStates,
    onLoading: () -> Unit,
    onError: (Throwable) -> Unit,
    onNotLoading: () -> Unit
) {
    LaunchedEffect(states) {
        val refreshState = states.refresh
        val appendState = states.append

        checkLoadState(
            refreshState,
            onLoading = onLoading,
            onError = onError,
            onNotLoading = {
                checkLoadState(
                    appendState,
                    onLoading = onLoading,
                    onError = onError,
                    onNotLoading = onNotLoading
                )
            }
        )
    }
}

fun checkLoadState(
    loadState: LoadState?,
    onLoading: () -> Unit,
    onError: (Throwable) -> Unit,
    onNotLoading: () -> Unit
) {
    when (loadState) {
        is LoadState.Loading -> onLoading()
        is LoadState.Error -> onError(loadState.error)
        is LoadState.NotLoading -> onNotLoading()
        else -> onNotLoading()
    }
}
