package com.rmakiyama.wishline.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Wishline's Material 3 theme. The brand palette is applied on every platform; Android dynamic
 * color is not used so the app keeps its own colors.
 */
@Composable
fun WlTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) WlDarkColorScheme else WlLightColorScheme
    val extendedColors = if (darkTheme) WlDarkExtendedColors else WlLightExtendedColors

    CompositionLocalProvider(LocalWlExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = WlTypography,
            shapes = WlShapes,
            content = content,
        )
    }
}

object WlTheme {
    val colorScheme: ColorScheme
        @Composable get() = MaterialTheme.colorScheme
    val extendedColors: WlExtendedColors
        @Composable get() = LocalWlExtendedColors.current
    val typography: Typography
        @Composable get() = MaterialTheme.typography
    val shapes: Shapes
        @Composable get() = MaterialTheme.shapes
    val spacing: Spacing
        @Composable get() = LocalSpacing.current
}
