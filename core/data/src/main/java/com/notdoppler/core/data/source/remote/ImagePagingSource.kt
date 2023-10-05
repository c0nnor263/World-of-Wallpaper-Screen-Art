package com.notdoppler.core.data.source.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.notdoppler.core.domain.domain.model.FetchedImage
import com.notdoppler.core.domain.domain.model.ImageRequestInfo
import com.notdoppler.core.domain.source.remote.repositories.RemoteImageSource
import javax.inject.Inject

class ImagePagingSource @Inject constructor(
    private val remoteImageSource: RemoteImageSource,
    private val imageRequestInfo: ImageRequestInfo,
) : PagingSource<Int, FetchedImage.Hit>() {
    override fun getRefreshKey(state: PagingState<Int, FetchedImage.Hit>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FetchedImage.Hit> {
        return try {
            val key = params.key ?: 1
            val response = remoteImageSource.getImagesByPage(key, imageRequestInfo.category)
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