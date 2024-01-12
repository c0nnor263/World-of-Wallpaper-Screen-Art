package com.doodle.turboracing3.presentation.composables

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.doodle.core.domain.enums.isNotPurchased
import com.doodle.core.ui.state.LocalRemoveAdsStatus
import com.doodle.core.ui.tweenEasy
import com.doodle.turboracing3.navigation.Screen
import com.doodle.turboracing3.presentation.navigation.AppHost

@Composable
fun AppContent() {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntry
    val removeAdsStatus = LocalRemoveAdsStatus.current
    Log.i("TAG", "AppContent: $removeAdsStatus")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(tweenEasy()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppHost(
            navController = navController, modifier = Modifier.fillMaxSize()
        )

        BottomBarContent(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            isVisible = backStackEntry?.destination?.route != Screen.Splash.route &&
                    removeAdsStatus.isNotPurchased()
        )

    }
}
