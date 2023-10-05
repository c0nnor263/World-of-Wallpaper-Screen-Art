package com.notdoppler.earntod.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController


@Composable
fun AppContent() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            // TODO: Remove for showing bottom bar content
            // BottomBarContent()
        },
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}