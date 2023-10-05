package com.notdoppler.core.billing.domain.model

import com.android.billingclient.api.ProductDetails

data class ProductDetailsInfo(
    val inAppDetails: List<ProductDetails>?,
    val subscriptionDetails: List<ProductDetails>?
)





