package com.doodle.core.data.source.remote.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.doodle.core.domain.model.remote.ImageRequestInfo
import com.doodle.core.domain.model.remote.RemoteImage
import com.doodle.core.domain.source.remote.RemoteImagePixabaySource

class RemoteImagePagingSource(
    private val remoteImagePixabaySource: RemoteImagePixabaySource,
    private val info: ImageRequestInfo
) : PagingSource<Int, RemoteImage.Hit>() {
    override fun getRefreshKey(state: PagingState<Int, RemoteImage.Hit>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RemoteImage.Hit> {
        return try {
            val key = params.key ?: info.pageKey
            val response = remoteImagePixabaySource.getImagesByPage(info.copy(pageKey = key))
            val hits = response.hits ?: throw Exception("No data")
            LoadResult.Page(
                data = hits,
                prevKey = if (key == 1) null else key - 1,
                nextKey = if (hits.isEmpty()) null else key + 1
            )
        } catch (e: Exception) {
            Log.i("TAG", "Exception: ${e.stackTraceToString()}")
            LoadResult.Error(e)
        }
    }
}
