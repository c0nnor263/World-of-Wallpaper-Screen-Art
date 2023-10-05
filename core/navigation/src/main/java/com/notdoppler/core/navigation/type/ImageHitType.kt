package com.notdoppler.core.navigation.type

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.notdoppler.core.domain.model.FetchedImage

class ImageHitType : NavType<FetchedImage.Hit>(isNullableAllowed = false) {
  override fun get(bundle: Bundle, key: String): FetchedImage.Hit? {
    return bundle.getParcelable(key)
  }
  override fun parseValue(value: String): FetchedImage.Hit {
    return Gson().fromJson(value, FetchedImage.Hit::class.java)
  }
  override fun put(bundle: Bundle, key: String, value: FetchedImage.Hit) {
    bundle.putParcelable(key, value)
  }
}