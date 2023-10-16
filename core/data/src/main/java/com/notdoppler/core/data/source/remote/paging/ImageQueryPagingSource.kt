package com.notdoppler.core.data.source.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import com.notdoppler.core.domain.source.remote.repository.RemoteImageSource
import javax.inject.Inject

class ImageQueryPagingSource @Inject constructor(
    private val remoteImageSource: RemoteImageSource,
    private val info: ImageRequestInfo
) : PagingSource<Int, FetchedImage.Hit>() {
    override fun getRefreshKey(state: PagingState<Int, FetchedImage.Hit>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FetchedImage.Hit> {
        return try {
            val key = params.key ?: 1
            val response =
                remoteImageSource.getImagesByQuery(info.copy(pageKey = key))
            val hits = response.hits ?: throw Exception("No data")
            LoadResult.Page(
                data = hits,
                prevKey = if (key == 1) null else key - 1,
                nextKey = if (hits.isEmpty()) null else key + 1
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}