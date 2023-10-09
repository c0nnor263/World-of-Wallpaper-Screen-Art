package com.notdoppler.feature.home.presentation.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.notdoppler.core.ui.R
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenScaffold(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    content: @Composable (PaddingValues) -> Unit
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = com.notdoppler.core.domain.R.string.app_name)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_menu_24),
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.displayCutoutPadding()
            )
        }, modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        content(innerPadding)
    }
}
