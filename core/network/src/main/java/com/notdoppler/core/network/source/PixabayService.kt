package com.notdoppler.core.network.source

import com.notdoppler.core.data.API_KEY
import com.notdoppler.core.domain.model.FetchedImage
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Locale

interface PixabayService {

    @GET("api")
    suspend fun getImagesByPage(
        @Query(value = "key") key: String = API_KEY,
        @Query(value = "page") pageKey: Int = 1,
        @Query(value = "q", encoded = true) q: String? = null,
        @Query(value = "lang") lang: String = Locale.getDefault().country.lowercase(),
        @Query(value = "orientation") orientation: String = "vertical",
        @Query(value = "category") category: String? = null,
        @Query(value = "order") order: String? = null,
        @Query(value = "per_page") perPage: Int = 20,
        @Query(value = "safesearch") safesearch: Boolean = true,
    ): FetchedImage

}