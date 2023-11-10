package com.notdoppler.core.domain.source.remote.repository

import com.notdoppler.core.domain.model.remote.FetchedImage

interface TagImageRepository {
    suspend fun getImageByTag(tag: String): FetchedImage.Hit?
}