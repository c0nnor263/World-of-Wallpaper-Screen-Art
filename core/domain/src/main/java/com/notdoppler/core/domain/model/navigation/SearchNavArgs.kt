package com.notdoppler.core.domain.model.navigation

import android.net.Uri
import android.os.Parcelable
import com.google.gson.Gson
import com.notdoppler.core.domain.enums.PagingKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchNavArgs(
    val query: String,
    val pagingKey: PagingKey = PagingKey.POPULAR,
) : Parcelable {
    override fun toString(): String {
        return Uri.encode(Gson().toJson(this))
    }
}
