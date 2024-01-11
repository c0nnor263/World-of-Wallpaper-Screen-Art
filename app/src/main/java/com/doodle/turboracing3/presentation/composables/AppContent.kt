package com.doodle.turboracing3.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.navigation.compose.rememberNavController
import com.doodle.core.ui.tweenEasy
import com.doodle.core.ui.tweenLong
import com.doodle.turboracing3.navigation.Screen
import com.doodle.turboracing3.presentation.navigation.AppHost

@Composable
fun AppContent() {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntry
    Column(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(tweenEasy()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppHost(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )

        AnimatedVisibility(
            visible = backStackEntry?.destination?.route != Screen.Splash.route,
            enter = slideInVertically(tweenLong()) { it },
            modifier = Modifier
                .zIndex(1F)
        ) {
            BottomBarContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        }
    }
}
