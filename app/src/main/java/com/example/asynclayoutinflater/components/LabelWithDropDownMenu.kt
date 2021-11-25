package com.example.asynclayoutinflater.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun <T> LabelWithDropDownMenu(
    modifier: Modifier = Modifier,
    label: String,
    options: List<T>,
    selectedOption: MutableState<T>,
    afterValueChange: () -> Unit = {}
) {
    Row(modifier = modifier) {
        var expanded by remember { mutableStateOf(false) }
        val arrowRotationState by animateFloatAsState(
            targetValue = if (expanded) 180f else 0f
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = label
        )
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            Row(
                modifier = Modifier
                    .width(80.dp)
                    .border(2.dp, MaterialTheme.colors.primary, RoundedCornerShape(8.dp))
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1F),
                    text = selectedOption.value.toString()
                )
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1F)
                        .rotate(arrowRotationState),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = ""
                )
            }
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                options.forEach {
                    DropdownMenuItem(
                        onClick = {
                            selectedOption.value = it
                            afterValueChange()
                            expanded = false
                        }
                    ) {
                        Text(text = it.toString())
                    }
                }
            }
        }
    }
}