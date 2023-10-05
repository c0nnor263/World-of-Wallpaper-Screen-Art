package com.notdoppler.feature.home.presentation.common

import androidx.compose.foundation.layout.mandatorySystemGesturesPadding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeModalNavigationDrawer(
    modifier: Modifier = Modifier,
    content: @Composable (DrawerState) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeModalNavigationDrawerContent()
        },
        gesturesEnabled = true,
        modifier = modifier.mandatorySystemGesturesPadding()
    ) {
        content(drawerState)
    }
}


@Composable
private fun HomeModalNavigationDrawerContent() {
    ModalDrawerSheet {

    }
}