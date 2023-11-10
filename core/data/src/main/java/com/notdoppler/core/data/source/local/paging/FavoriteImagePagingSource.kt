package com.notdoppler.core.data.source.local.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.notdoppler.core.database.dao.FavoriteImageDao
import com.notdoppler.core.domain.model.remote.FetchedImage

class FavoriteImagePagingSource(
    private val favoriteImageDao: FavoriteImageDao,
) : PagingSource<Int, FetchedImage.Hit>() {
    override fun getRefreshKey(state: PagingState<Int, FetchedImage.Hit>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FetchedImage.Hit> {
        return try {
            val key = params.key ?: 1
            val favoriteImages = favoriteImageDao.getAll()
            Log.i("TAG", "load: Favorite $key")
            val fetchedImageHit = favoriteImages.map {
                it.mapToFetchedImageHit()
            }

            LoadResult.Page(
                data = fetchedImageHit,
                prevKey = if (key == 1) null else key - 1,
                nextKey = if (favoriteImages.isEmpty()) null else key + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}