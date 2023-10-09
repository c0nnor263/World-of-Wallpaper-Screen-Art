package com.notdoppler.core.domain.model

import android.net.Uri
import android.os.Parcelable
import com.google.gson.Gson
import com.notdoppler.core.domain.presentation.TabOrder
import kotlinx.parcelize.Parcelize

@Parcelize
data class PictureDetailsNavArgs(
    val selectedImageIndex: Int,
    val tabOrder: TabOrder
) : Parcelable {

    override fun toString(): String {
        return Uri.encode(Gson().toJson(this))
    }
}
