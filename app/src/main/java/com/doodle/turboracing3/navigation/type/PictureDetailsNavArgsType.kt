package com.doodle.turboracing3.navigation.type

import android.os.Bundle
import androidx.navigation.NavType
import com.doodle.core.domain.getSafeParcelable
import com.doodle.core.domain.model.navigation.PictureDetailsNavArgs
import com.google.gson.Gson

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
