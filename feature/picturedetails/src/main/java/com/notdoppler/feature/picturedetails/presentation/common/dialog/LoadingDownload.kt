package com.notdoppler.feature.picturedetails.presentation.common.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDownload(modifier: Modifier = Modifier, visible: Boolean) {
    AnimatedVisibility(modifier = modifier, visible = visible) {
        AlertDialog(
            onDismissRequest = { }, properties = DialogProperties(
                dismissOnBackPress = false, dismissOnClickOutside = false
            )
        ) {
            Card(shape = RoundedCornerShape(24.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(64.dp))
            }
        }
    }
}