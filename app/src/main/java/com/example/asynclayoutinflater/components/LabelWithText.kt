package com.example.asynclayoutinflater.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LabelWithText(
    modifier: Modifier = Modifier,
    label: String,
    text: MutableState<String>,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    singleLine: Boolean = true,
    textHeight: Dp = 45.dp,
    textWidth: Dp = 60.dp,
    afterValueChange: () -> Unit = {}
) {
    Row(modifier = modifier) {

        Text(
            modifier = Modifier.padding(8.dp),
            text = label
        )
        BasicTextField(
            modifier = Modifier
                .size(textWidth, textHeight)
                .border(2.dp, MaterialTheme.colors.primary, RoundedCornerShape(8.dp)),
            value = text.value,
            onValueChange = { text.value = it; afterValueChange() },
            keyboardOptions = keyboardOptions,
            cursorBrush = SolidColor(MaterialTheme.colors.primary),
            singleLine = singleLine,
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colors.onSurface
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    innerTextField()
                }
            }
        )
    }
}