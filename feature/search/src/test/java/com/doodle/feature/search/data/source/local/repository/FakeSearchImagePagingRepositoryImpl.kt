package com.doodle.feature.search.data.source.local.repository

import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.doodle.core.domain.model.remote.ImageRequestInfo
import com.doodle.core.domain.model.remote.RemoteImage
import com.doodle.core.domain.source.remote.repository.SearchImagePagingRepository

class FakeSearchImagePagingRepositoryImpl : SearchImagePagingRepository {
    private val items = listOf(
        RemoteImage.Hit(
            id = 1,
            pageURL = "https://pixabay.com/photos/rose-flower-petal-floral-noble-rose-819001/",
            type = "photo",
            tags = "rose, flower, petal",
            previewURL = "https://cdn.pixabay.com/photo/2015/04/10/00/41/rose-715540_150.jpg",
            previewWidth = 150,
            previewHeight = 84,
            webformatURL = "https://pixabay.com/get/52e0d3404e53a514f6da8c7dda79367b1c3cd7e4564c704c7c2d7edc9f4dc45f_640.jpg",
            webformatWidth = 640,
            webformatHeight = 360,
            largeImageURL = "https://pixabay.com/get/52e0d3404e53a514f6da8c7dda79367b1c3cd7e4564c704c7c2d7edc9f4dc45f_1280.jpg",
            imageWidth = 5184,
            imageHeight = 2916,
            imageSize = 2110736,
            views = 7671,
            downloads = 6439,
            likes = 15,
            comments = 5,
            user_id = 48777,
            user = "Josch13",
            userImageURL = "https://cdn.pixabay.com/user/2015/11/27/06-58-54-609_250x250.jpg"
        ),
        RemoteImage.Hit(
            id = 2,
            pageURL = "https://pixabay.com/photos/rose-flower-petal-floral-noble-rose-819001/",
            type = "photo",
            tags = "rose, flower, petal",
            previewURL = "https://cdn.pixabay.com/photo/2015/04/10/00/41/rose-715540_150.jpg",
            previewWidth = 150,
            previewHeight = 84,
            webformatURL = "https://pixabay.com/get/52e0d3404e53a514f6da8c7dda79367b1c3cd7e4564c704c7c2d7ed"
        ),
        RemoteImage.Hit(
            id = 3,
            pageURL = "https://pixabay.com/photos/rose-flower-petal-floral-noble-rose-819001/",
            type = "photo",
            tags = "rose, flower, petal",
            previewURL = "https://cdn.pixabay.com/photo/2015/04/10/00/41/rose-715540_150.jpg",
            previewWidth = 150,
            previewHeight = 84,
            webformatURL = "https://pixabay.com/get/52e0d3404e53a514f6da8c7dda79367b1c3cd7e4564c704c7c2d7ed"
        ),
        RemoteImage.Hit(
            id = 4,
            pageURL = "https://pixabay.com/photos/rose-flower-petal-floral-noble-rose-819001/",
            type = "photo",
            tags = "rose, flower, petal",
            previewURL = "https://cdn.pixabay.com/photo/2015/04/10/00/41/rose-715540_150.jpg",
            previewWidth = 150,
            previewHeight = 84,
            webformatURL = "https://pixabay.com/get/52e0d3404e53a514f6da8c7dda79367b1c3cd7e4564c704c7c2d7ed"
        )
    )

    override fun getPagingSource(info: ImageRequestInfo): PagingSource<Int, RemoteImage.Hit> {
        return items.asPagingSourceFactory().invoke()
    }
}
