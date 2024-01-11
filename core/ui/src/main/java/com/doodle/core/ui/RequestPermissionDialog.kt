package com.doodle.core.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.doodle.core.ui.card.CardButton
import com.doodle.core.ui.state.DialogState

@Composable
fun RequestPermissionDialog(
    modifier: Modifier = Modifier,
    state: DialogState,
    permission: String,
    @StringRes requestTitleMessage: Int,
    @StringRes requestContentMessage: Int,
    onDismiss: () -> Unit,
) {
    val requestPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        onDismiss()
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = state.isVisible,
        enter = fadeIn(tweenMedium()) + scaleIn(tweenMedium()),
        exit = scaleOut(tweenMedium()) + fadeOut()
    ) {
        Dialog(
            onDismissRequest = onDismiss, properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = stringResource(
                            requestTitleMessage
                        ),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    Text(
                        text = stringResource(
                            requestContentMessage,
                            stringResource(
                                id = com.doodle.core.domain.R.string.app_name
                            )
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 5
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CardButton(onClick = {
                            requestPermission.launch(permission)
                        }
                        ) {
                            Text(
                                stringResource(R.string.yes),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }

                        Spacer(
                            Modifier.width(8.dp)
                        )

                        CardButton(onClick = onDismiss) {
                            Text(
                                stringResource(R.string.no),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}


