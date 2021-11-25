package com.example.asynclayoutinflater.components

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CustomSnackbar(
    modifier: Modifier = Modifier,
    snackState: MutableState<SnackbarHostState>
) {
    SnackbarHost(
        hostState = snackState.value,
        modifier = modifier,
    )
}

fun showSnackbar(
    snackState: SnackbarHostState,
    message: String,
    actionLabel: String? = null,
    onActionPerformed: () -> Unit = {},
    onDismissed: () -> Unit = {},
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    CoroutineScope(Dispatchers.Main).launch {
        var result = snackState.showSnackbar(message, actionLabel, duration)
        when(result) {
            SnackbarResult.ActionPerformed -> {
                onActionPerformed()
            }
            SnackbarResult.Dismissed -> {
                onDismissed()
            }
        }
    }
}