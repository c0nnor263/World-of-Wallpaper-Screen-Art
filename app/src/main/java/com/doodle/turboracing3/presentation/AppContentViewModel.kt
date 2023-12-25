package com.doodle.turboracing3.presentation

import androidx.lifecycle.ViewModel
import com.doodle.core.billing.data.BillingDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppContentViewModel @Inject constructor(
    private val billingDataSource: BillingDataSource
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        billingDataSource.endConnection()
    }

    init {
        billingDataSource.initClient()
    }

    fun onResumeBilling() {
        billingDataSource.onResumeBilling()
    }
}
