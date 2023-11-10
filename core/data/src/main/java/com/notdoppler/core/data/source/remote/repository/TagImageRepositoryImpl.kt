package com.notdoppler.core.data.source.remote.repository

import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.source.remote.RetrofitPixabayService
import com.notdoppler.core.domain.source.remote.repository.TagImageRepository
import javax.inject.Inject

class TagImageRepositoryImpl @Inject constructor(
    private val pixabayService: RetrofitPixabayService,
) : TagImageRepository {
    override suspend fun getImageByTag(tag: String): FetchedImage.Hit? {
        return try {
            val result = pixabayService.getImagesByPage(q = tag)
            result.hits?.get(0)
        } catch (e: Exception) {
            null
        }
    }
}