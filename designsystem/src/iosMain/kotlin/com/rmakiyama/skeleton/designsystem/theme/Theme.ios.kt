package com.rmakiyama.skeleton.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
internal actual fun platformColorScheme(darkTheme: Boolean): ColorScheme =
    if (darkTheme) DarkDefaultColorScheme else LightDefaultColorScheme
