package com.notdoppler.core.billing.domain.model

import com.android.billingclient.api.ProductDetails
import kotlinx.collections.immutable.ImmutableList

data class ProductDetailsInfo(
    val inAppDetails: ImmutableList<ProductDetails>?,
    val subscriptionDetails: ImmutableList<ProductDetails>?,
)





