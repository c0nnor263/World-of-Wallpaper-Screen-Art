package com.notdoppler.core.domain.source.remote

import com.notdoppler.core.domain.BuildConfig
import com.notdoppler.core.domain.model.remote.FetchedImage
import java.util.Locale

interface RetrofitPixabayService {
    suspend fun getImagesByPage(
        key: String = BuildConfig.API_KEY,
        pageKey: Int = 1,
        q: String? = null,
        lang: String = Locale.getDefault().country.lowercase(),
        orientation: String = "vertical",
        category: String? = null,
        order: String? = null,
        perPage: Int = 20,
        safesearch: Boolean = true,
    ): FetchedImage
}