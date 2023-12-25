package com.doodle.turboracing3.navigation.type

import android.os.Bundle
import androidx.navigation.NavType
import com.doodle.core.domain.getSafeParcelable
import com.doodle.core.domain.model.navigation.SearchNavArgs
import com.google.gson.Gson

class SearchNavArgsType : NavType<SearchNavArgs?>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): SearchNavArgs? {
        return bundle.getSafeParcelable(key)
    }

    override fun parseValue(value: String): SearchNavArgs? {
        return Gson().fromJson(value, SearchNavArgs::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: SearchNavArgs?) {
        bundle.putParcelable(key, value)
    }
}
