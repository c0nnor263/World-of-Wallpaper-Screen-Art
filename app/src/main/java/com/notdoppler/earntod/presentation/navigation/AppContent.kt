package com.notdoppler.earntod.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.notdoppler.earntod.presentation.composables.BottomBarContent


@Composable
fun AppContent() {
    val navController = rememberNavController()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .weight(1F)
        )
        BottomBarContent(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        )
    }
}