package com.doodle.core.ui.state

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.util.concurrent.TimeUnit


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

    fun showFor(seconds: Int) {
        isVisible = true
        Handler(
            Looper.getMainLooper()
        ).postDelayed(
            {
                isVisible = false
            }, TimeUnit.SECONDS.toMillis(seconds.toLong())
        )
    }

    fun dismiss() {
        isVisible = false
    }
}