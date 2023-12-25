package com.doodle.core.data.source.local

import android.content.Context
import com.doodle.core.domain.source.local.StringResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StringResourceProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) :
    StringResourceProvider {
    override fun getString(resourceId: Int, vararg parameters: String): String {
        return context.getString(resourceId, *parameters)
    }
}
