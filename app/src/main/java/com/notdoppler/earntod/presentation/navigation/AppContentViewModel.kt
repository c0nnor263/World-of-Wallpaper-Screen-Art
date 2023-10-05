package com.notdoppler.earntod.presentation.navigation

import androidx.lifecycle.ViewModel
import com.notdoppler.core.billing.data.BillingDataSource
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

    fun onResumeBilling(){
        billingDataSource.onResumeBilling()
    }

}