package com.notdoppler.earntod.navigation.type

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.notdoppler.core.domain.getSafeParcelable
import com.notdoppler.core.domain.model.navigation.SearchNavArgs

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