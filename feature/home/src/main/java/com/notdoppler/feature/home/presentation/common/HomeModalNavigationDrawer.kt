package com.notdoppler.feature.home.presentation.common

import androidx.compose.foundation.layout.mandatorySystemGesturesPadding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.foundation.layout.systemGesturesPadding
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
        gesturesEnabled  = drawerState.isOpen,
        modifier = modifier
    ) {
        content(drawerState)
    }
}


@Composable
private fun HomeModalNavigationDrawerContent() {
    ModalDrawerSheet {

    }
}