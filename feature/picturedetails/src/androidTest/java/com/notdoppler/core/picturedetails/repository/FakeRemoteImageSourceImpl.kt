package com.notdoppler.core.picturedetails.repository

import com.google.gson.Gson
import com.notdoppler.core.domain.model.remote.FetchedImage
import com.notdoppler.core.domain.model.remote.ImageRequestInfo
import com.notdoppler.core.domain.source.remote.RemoteImageSource
import javax.inject.Inject

class FakeRemoteImageSourceImpl @Inject constructor() : RemoteImageSource {
    override suspend fun getImagesByPage(info: ImageRequestInfo): FetchedImage {
        return Gson().fromJson(
            "{\n" +
                    "\"total\": 4692,\n" +
                    "\"totalHits\": 500,\n" +
                    "\"hits\": [\n" +
                    "    {\n" +
                    "        \"id\": 195893,\n" +
                    "        \"pageURL\": \"https://pixabay.com/en/blossom-bloom-flower-195893/\",\n" +
                    "        \"type\": \"photo\",\n" +
                    "        \"tags\": \"blossom, bloom, flower\",\n" +
                    "        \"previewURL\": \"https://cdn.pixabay.com/photo/2013/10/15/09/12/flower-195893_150.jpg\",\n" +
                    "        \"previewWidth\": 150,\n" +
                    "        \"previewHeight\": 84,\n" +
                    "        \"webformatURL\": \"https://pixabay.com/get/35bbf209e13e39d2_640.jpg\",\n" +
                    "        \"webformatWidth\": 640,\n" +
                    "        \"webformatHeight\": 360,\n" +
                    "        \"largeImageURL\": \"https://pixabay.com/get/ed6a99fd0a76647_1280.jpg\",\n" +
                    "        \"fullHDURL\": \"https://pixabay.com/get/ed6a9369fd0a76647_1920.jpg\",\n" +
                    "        \"imageURL\": \"https://pixabay.com/get/ed6a9364a9fd0a76647.jpg\",\n" +
                    "        \"imageWidth\": 4000,\n" +
                    "        \"imageHeight\": 2250,\n" +
                    "        \"imageSize\": 4731420,\n" +
                    "        \"views\": 7671,\n" +
                    "        \"downloads\": 6439,\n" +
                    "        \"likes\": 5,\n" +
                    "        \"comments\": 2,\n" +
                    "        \"user_id\": 48777,\n" +
                    "        \"user\": \"Josch13\",\n" +
                    "        \"userImageURL\": \"https://cdn.pixabay.com/user/2013/11/05/02-10-23-764_250x250.jpg\"\n" +
                    "    }\n" +
                    "]\n" +
                    "}", FetchedImage::class.java
        )
    }

    override suspend fun getImagesByQuery(info: ImageRequestInfo): FetchedImage {
        return FetchedImage(
            total = 0,
            totalHits = 0,
            hits = emptyList()
        )
    }
}