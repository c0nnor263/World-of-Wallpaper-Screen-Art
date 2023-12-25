package com.doodle.core.network.source

import com.doodle.core.domain.model.remote.RemoteImage
import com.doodle.core.network.BuildConfig
import java.util.Locale
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayService {
    // TODO: Refactor to 7 parameters
    @GET("api")
    suspend fun getImagesByPage(
        @Query(value = "key") key: String = BuildConfig.API_KEY,
        @Query(value = "page") pageKey: Int = 1,
        @Query(value = "q", encoded = true) q: String? = null,
        @Query(value = "lang") lang: String = Locale.getDefault().country.lowercase(),
        @Query(value = "orientation") orientation: String = "vertical",
        @Query(value = "category") category: String? = null,
        @Query(value = "order") order: String? = null,
        @Query(value = "per_page") perPage: Int = 20,
        @Query(value = "safesearch") safesearch: Boolean = true,
        @Query(value = "editors_choice") editorsChoice: Boolean = false
    ): RemoteImage
}
