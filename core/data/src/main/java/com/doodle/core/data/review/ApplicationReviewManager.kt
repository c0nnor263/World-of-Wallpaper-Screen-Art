package com.doodle.core.data.review

import android.content.Context
import androidx.activity.ComponentActivity
import com.doodle.core.data.di.IoDispatcher
import com.doodle.core.domain.source.local.repository.AppPreferencesDataStoreRepository
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApplicationReviewManager @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val appPreferencesDataStoreRepository: AppPreferencesDataStoreRepository
) {
    private val manager = ReviewManagerFactory.create(applicationContext)

    private suspend fun isAvailableForReview(): Boolean = withContext(ioDispatcher) {
        appPreferencesDataStoreRepository.getIsAvailableForReview()
    }

    suspend fun requestInfo(activity: ComponentActivity) {

        if (isAvailableForReview()) {
            manager.requestReviewFlow().addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val reviewInfo: ReviewInfo? = task.result
                    startReviewFlow(activity, reviewInfo)

                    CoroutineScope(SupervisorJob() + ioDispatcher).launch {
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
