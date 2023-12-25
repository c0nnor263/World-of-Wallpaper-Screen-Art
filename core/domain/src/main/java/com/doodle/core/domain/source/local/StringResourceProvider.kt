package com.doodle.core.domain.source.local

import androidx.annotation.StringRes

fun interface StringResourceProvider {
    fun getString(@StringRes resourceId: Int, vararg parameters: String): String
}
