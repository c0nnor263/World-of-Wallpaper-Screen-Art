package com.notdoppler.earntod.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.notdoppler.core.ui.tweenLong
import com.notdoppler.earntod.navigation.Screen
import com.notdoppler.earntod.presentation.navigation.AppHost


@Composable
fun AppContent() {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntry
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .weight(1F)
        )
        AnimatedVisibility(
            visible = backStackEntry?.destination?.route != Screen.Splash.route,
            enter = slideInVertically(tweenLong()) { it },
        ) {
            BottomBarContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        }
    }
}