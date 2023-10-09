package com.notdoppler.core.navigation.type

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.notdoppler.core.domain.getSafeParcelable
import com.notdoppler.core.domain.model.PictureDetailsNavArgs


class PictureDetailsNavArgsType : NavType<PictureDetailsNavArgs>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): PictureDetailsNavArgs? {
        return bundle.getSafeParcelable(key)
    }

    override fun parseValue(value: String): PictureDetailsNavArgs {
        return Gson().fromJson(value, PictureDetailsNavArgs::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: PictureDetailsNavArgs) {
        bundle.putParcelable(key, value)
    }
}