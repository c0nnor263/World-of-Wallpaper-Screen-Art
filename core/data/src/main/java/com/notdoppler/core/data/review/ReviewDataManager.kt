package com.notdoppler.core.data.review

import android.content.Context
import androidx.activity.ComponentActivity
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.notdoppler.core.domain.source.local.repository.AppPreferencesDataStoreRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReviewDataManager @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val appPreferencesDataStoreRepository: AppPreferencesDataStoreRepository,
) {
    private val manager = ReviewManagerFactory.create(applicationContext)


    private suspend fun isAvailableForReview(): Boolean = withContext(Dispatchers.IO) {
        appPreferencesDataStoreRepository.getIsAvailableForReview()
    }

    suspend fun requestReviewInfo(activity: ComponentActivity) {

        if (isAvailableForReview()) {
            manager.requestReviewFlow().addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val reviewInfo: ReviewInfo? = task.result
                    startReviewFlow(activity, reviewInfo)

                    CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                        appPreferencesDataStoreRepository.setIsAvailableForReview(false)
                    }
                }
            }
        }
    }

    private fun startReviewFlow(activity: ComponentActivity, reviewInfo: ReviewInfo?) {
        reviewInfo?.let { info ->
            manager.launchReviewFlow(activity, info).addOnCompleteListener {}
        }
    }
}