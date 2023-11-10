package com.notdoppler.core.domain.source.local

import androidx.annotation.StringRes

interface StringResourceProvider {
    fun getString(@StringRes resourceId: Int, vararg parameters: String): String
}