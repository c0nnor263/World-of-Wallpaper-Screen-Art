package com.notdoppler.core.billing.domain.model

import com.notdoppler.core.billing.domain.enums.BillingProductType
import com.notdoppler.core.billing.domain.enums.VerifyResult

data class PurchaseProduct(
    val type: BillingProductType? = null,
    val result: VerifyResult? = null,
)