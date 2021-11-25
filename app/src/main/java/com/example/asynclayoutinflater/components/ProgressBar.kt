package com.example.asynclayoutinflater.components

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun ProgressBar(visible: MutableState<Boolean>) {
    if(visible.value) {
        CircularProgressIndicator()
    }
}