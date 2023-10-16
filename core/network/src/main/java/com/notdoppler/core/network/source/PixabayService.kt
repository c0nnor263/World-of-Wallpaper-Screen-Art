package com.notdoppler.core.network.source

import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.source.remote.RetrofitPixabayService
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayService : RetrofitPixabayService {

    @GET("api")
    override suspend fun getImagesByPage(
        @Query(value = "key") key: String,
        @Query(value = "page") pageKey: Int,
        @Query(value = "q", encoded = true) q: String?,
        @Query(value = "lang") lang: String,
        @Query(value = "orientation") orientation: String,
        @Query(value = "category") category: String?,
        @Query(value = "order") order: String?,
        @Query(value = "per_page") perPage: Int,
        @Query(value = "safesearch") safesearch: Boolean,
    ): FetchedImage

}