package com.example.asynclayoutinflater.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.example.asynclayoutinflater.R

@Composable
fun DarkColorPalette() = darkColors(
    primary = colorResource(id = R.color.colorPrimary),
    primaryVariant = colorResource(id = R.color.colorPrimaryVariant),
    secondary = colorResource(id = R.color.colorSecondary),
    secondaryVariant = colorResource(id = R.color.colorSecondaryVariant),
    background = colorResource(id = R.color.colorBackground),
    surface = colorResource(id = R.color.colorSurface),
    error = colorResource(id = R.color.colorError),
    onPrimary = colorResource(id = R.color.colorOnPrimary),
    onSecondary = colorResource(id = R.color.colorOnSecondary),
    onBackground = colorResource(id = R.color.colorOnBackground),
    onSurface = colorResource(id = R.color.colorOnSurface),
    onError = colorResource(id = R.color.colorOnError)
)

@Composable
fun AsyncLayoutInflaterTheme(
    content: @Composable() () -> Unit
) {

    MaterialTheme(
            colors = DarkColorPalette(),
            typography = Typography,
            shapes = Shapes,
            content = content
    )
}