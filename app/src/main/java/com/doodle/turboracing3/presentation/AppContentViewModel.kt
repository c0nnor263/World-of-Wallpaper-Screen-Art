package com.doodle.turboracing3.presentation

import androidx.lifecycle.ViewModel
import com.doodle.core.billing.data.BillingDataSource
import com.doodle.core.domain.source.local.repository.UserPreferencesDataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppContentViewModel @Inject constructor(
    private val billingDataSource: BillingDataSource,
    private val userPreferencesDataStoreRepository: UserPreferencesDataStoreRepository
) : ViewModel() {

    val isPremiumUser = userPreferencesDataStoreRepository.getRemoveAdsStatusFlow()

    init {
        billingDataSource.initClient()
    }

    override fun onCleared() {
        super.onCleared()
        billingDataSource.endConnection()
    }

    fun onResumeBilling() {
        billingDataSource.onResumeBilling()
    }
}
