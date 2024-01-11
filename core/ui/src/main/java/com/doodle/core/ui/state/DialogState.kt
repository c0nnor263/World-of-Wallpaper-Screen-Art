package com.doodle.core.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@Composable
fun rememberDialogState(initial: Boolean = false): DialogState {
    return remember {
        DialogState(initial)
    }
}

class DialogState(initial: Boolean) {
    var isVisible by mutableStateOf(initial)
        private set

    fun show() {
        isVisible = true
    }

    fun dismiss() {
        isVisible = false
    }
}