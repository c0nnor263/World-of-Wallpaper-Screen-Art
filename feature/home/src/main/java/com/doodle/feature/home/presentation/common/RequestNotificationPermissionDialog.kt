package com.doodle.feature.home.presentation.common

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.doodle.core.ui.state.DialogState

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RequestNotificationPermissionDialog(
    modifier: Modifier = Modifier,
    state: DialogState,
    onDismiss: () -> Unit
) {
    val requestPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        onDismiss()
    }

    AnimatedVisibility(modifier = modifier, visible = state.isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = stringResource(
                        com.doodle.core.ui.R.string.request_notification_title
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = stringResource(
                        com.doodle.core.ui.R.string.request_notification_permission_message,
                        stringResource(
                            id = com.doodle.core.domain.R.string.app_name
                        )
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TextButton(
                        onClick = {
                            requestPermission.launch(
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        }
                    ) {
                        Text(
                            stringResource(com.doodle.core.ui.R.string.yes),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }

                    Spacer(
                        Modifier.width(8.dp)
                    )

                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(
                            stringResource(com.doodle.core.ui.R.string.no),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                }
            }
        }
    }
}