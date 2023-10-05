package com.notdoppler.core.billing.data

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchaseHistory
import com.android.billingclient.api.queryPurchasesAsync
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.notdoppler.core.billing.domain.enums.BillingProductType
import com.notdoppler.core.billing.domain.enums.VerifyResult
import com.notdoppler.core.billing.domain.model.ProductDetailsInfo
import com.notdoppler.core.billing.domain.model.PurchaseProduct
import com.notdoppler.core.domain.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ActivityRetainedScoped
class BillingDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val scope: CoroutineScope,
) {
    private val initialBillingStartTime = System.currentTimeMillis()
    var fetchDelay: Long = TimeUnit.SECONDS.toMillis(1)


    // Repository Pattern
    private val _productsDetailsFlow = MutableStateFlow(ProductDetailsInfo(null, null))
    val productsDetailsFlow: StateFlow<ProductDetailsInfo> = _productsDetailsFlow.asStateFlow()

    private val _pendingBenefitFlow = MutableStateFlow(PurchaseProduct())
    val pendingBenefitFlow: StateFlow<PurchaseProduct> = _pendingBenefitFlow.asStateFlow()

    private val billingStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                scope.launch(Dispatchers.IO) {
                    queryProductDetails()
                }
            }
        }

        override fun onBillingServiceDisconnected() {
            scope.launch {
                delay(fetchDelay)
                initClient()

                val timePass = System.currentTimeMillis() - initialBillingStartTime
                if (timePass > TimeUnit.SECONDS.toMillis(30)) {
                    fetchDelay = TimeUnit.SECONDS.toMillis(1)
                } else fetchDelay += fetchDelay
            }
        }
    }
    private val purchasesListener = PurchasesUpdatedListener { billingResult, purchases ->
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged.not()) {
                        verifyPurchase(purchase)
                    }
                }
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                updatePendingBenefitResult(VerifyResult.CANCELLED)
            }

            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                initClient()
            }

            else -> {}
        }
    }

    private val client =
        BillingClient.newBuilder(context)
            .setListener(purchasesListener)
            .enablePendingPurchases()
            .build()


    fun initClient() {
        client.startConnection(billingStateListener)
    }

    private suspend fun queryPurchases() = withContext(Dispatchers.IO) {
        val inAppParams =
            QueryPurchasesParams.newBuilder().setProductType(ProductType.INAPP).build()
        val subscriptionParams =
            QueryPurchasesParams.newBuilder().setProductType(ProductType.SUBS).build()

        val inAppProductsResult = client.queryPurchasesAsync(inAppParams)
        val subscriptionsResult = client.queryPurchasesAsync(subscriptionParams)

        if (inAppProductsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            inAppProductsResult.purchasesList.forEach { purchase ->
                verifyPurchase(purchase)
            }
        }

        if (subscriptionsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            subscriptionsResult.purchasesList.forEach { purchase ->
                verifyPurchase(purchase)
            }
        }
    }

    suspend fun queryProductDetails() = withContext(Dispatchers.IO) {
        val inAppParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                BillingProductType.values()
                .filter { it.productType == ProductType.INAPP }
                .map {
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(it.id)
                        .setProductType(ProductType.INAPP)
                        .build()
                }
            )
            .build()

        val subscriptionParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                BillingProductType.values()
                .filter { it.productType == ProductType.SUBS }
                .map {
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(it.id)
                        .setProductType(ProductType.SUBS)
                        .build()
                }
            ).build()

        val inAppDetailsResult = client.queryProductDetails(inAppParams)
        val subscriptionDetailsResult = client.queryProductDetails(subscriptionParams)


        if (inAppDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            val result = inAppDetailsResult.productDetailsList
            _productsDetailsFlow.value = _productsDetailsFlow.value.copy(
                inAppDetails = result
            )
        }
        if (subscriptionDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            val result = subscriptionDetailsResult.productDetailsList
            _productsDetailsFlow.value =
                _productsDetailsFlow.value.copy(subscriptionDetails = result)
        }
    }

    fun purchaseProduct(
        details: ProductDetails,
        type: BillingProductType,
        onRequestActivity: () -> ComponentActivity
    ) {
        Log.i("TAG", "purchaseProduct: $details")
        try {
            val productDetailsParamsList =
                BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(details).build()
            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParamsList)).build()
            _pendingBenefitFlow.value = PurchaseProduct(type, VerifyResult.PENDING)
            client.launchBillingFlow(onRequestActivity(), flowParams)
        } catch (e: Exception) {
            _pendingBenefitFlow.value = PurchaseProduct(type, VerifyResult.FAILED)
        }
    }


    private fun verifyPurchase(purchase: Purchase?) {
        val purchaseJson = purchase?.originalJson?.let { JSONObject(it) }
        val productId = purchaseJson?.getString("productId")
        val type =ProductType.INAPP
        Log.i("TAG", "verifyPurchase: $purchase")
        val url =
//            BuildConfig.verifyPurchases + "?" + // TODO verifyPurchases
                    "purchaseToken=${purchase?.purchaseToken}&" +
                    "productId=$productId&" +
                    "productType=$type"
        Log.i("TAG", "verifyPurchase: $url")

        val responseListener = Response.Listener<String> { response ->
            Log.i("TAG", "verifyPurchase: response $response")
            val isValid = JSONObject(response).getBoolean("isValid").also {
                Log.i("TAG", "verifyPurchase: $it")
            }
            if (isValid) {
                updatePendingBenefitType(purchaseJson)
                when (productId) {
                    BillingProductType.REMOVE_ADS.id -> {
                        acknowledgePurchase(purchase)
                    }

                    else -> consumePurchase(purchase)
                }
            } else updatePendingBenefitResult(VerifyResult.FAILED)

        }

        val request = StringRequest(
            Request.Method.POST, url, responseListener
        ) { error ->
            Log.i(
                "TAG",
                "verifyPurchase: net error ${error.networkResponse.statusCode} \n${error.message}"
            )
            when (productId) {
                BillingProductType.REMOVE_ADS.id, -> {
                    updatePendingBenefitResult(VerifyResult.FAILED)
                }

                else -> {
                    updatePendingBenefitType(purchaseJson)
                    consumePurchase(purchase)
                }
            }
        }

        Volley.newRequestQueue(context).add(request)
    }


    private fun receiveProduct(type: BillingProductType?) = scope.launch {
        when (type) {
            BillingProductType.REMOVE_ADS -> {
            }

            else -> {}
        }
    }

    private fun consumePurchase(purchase: Purchase?) {
        Log.i("TAG", "consumePurchase: $purchase")
        val params =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase?.purchaseToken ?: "")
                .build()
        scope.launch {
            val consumeResult = client.consumePurchase(params)
            Log.i("TAG", "consumePurchase: $consumeResult")
            if (consumeResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                updatePendingBenefitResult(VerifyResult.SUCCESS)
            } else {
                updatePendingBenefitResult(VerifyResult.FAILED)
            }
        }
    }


    private fun acknowledgePurchase(purchase: Purchase?) {
        Log.i("TAG", "acknowledgePurchase: $purchase")
        if (purchase?.isAcknowledged == true) {
            updatePendingBenefitResult(VerifyResult.SUCCESS)
            return
        }

        val acknowledgeParams =
            AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase?.purchaseToken ?: "")
                .build()
        scope.launch {
            val acknowledgeResult = client.acknowledgePurchase(acknowledgeParams)
            Log.i("TAG", "acknowledgePurchase: $acknowledgeResult")
            if (acknowledgeResult.responseCode == BillingClient.BillingResponseCode.OK) {
                updatePendingBenefitResult(VerifyResult.SUCCESS)
            } else {
                updatePendingBenefitResult(VerifyResult.FAILED)
            }
        }
    }

    private fun updatePendingBenefitResult(result: VerifyResult) {
        _pendingBenefitFlow.value = pendingBenefitFlow.value.copy(result = result)

        if (result == VerifyResult.SUCCESS) {
            receiveProduct(pendingBenefitFlow.value.type)
        }
    }

    private fun updatePendingBenefitType(json: JSONObject?) {
        val type = BillingProductType.entries.find { billingProductType ->
            json?.optString("productId") == billingProductType.id
        }
        _pendingBenefitFlow.value = pendingBenefitFlow.value.copy(type = type)
    }

    fun endConnection() {
        client.endConnection()
    }

    fun onResumeBilling() {
        if (client.isReady) {
            scope.launch {
                queryProductDetails()
            }
        }
    }

    fun restorePurchases() = scope.launch {
        val inAppHistoryParams = QueryPurchaseHistoryParams.newBuilder().setProductType(
            ProductType.INAPP
        ).build()

        val subscriptionHistoryParams = QueryPurchaseHistoryParams.newBuilder().setProductType(
            ProductType.SUBS
        ).build()

        val inAppResult = client.queryPurchaseHistory(inAppHistoryParams)
        val subscriptionResult = client.queryPurchaseHistory(subscriptionHistoryParams)
        if (inAppResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK || subscriptionResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            queryPurchases()
        }
    }


}