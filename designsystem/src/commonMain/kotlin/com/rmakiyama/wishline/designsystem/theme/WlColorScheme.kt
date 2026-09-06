package com.rmakiyama.wishline.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Material 3 color scheme seeded from lime green. */
internal val WlLightColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF237A3F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC4F7CD),
    onPrimaryContainer = Color(0xFF003912),
    inversePrimary = Color(0xFF8CE59B),
    secondary = Color(0xFF4F6354),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDDF3E0),
    onSecondaryContainer = Color(0xFF0F2E18),
    tertiary = Color(0xFFB85E00),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDCC4),
    onTertiaryContainer = Color(0xFF3B1A00),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF6FFF6),
    onBackground = Color(0xFF14211A),
    surface = Color(0xFFF6FFF6),
    onSurface = Color(0xFF14211A),
    surfaceVariant = Color(0xFFDDE5DD),
    onSurfaceVariant = Color(0xFF43554A),
    surfaceTint = Color(0xFF237A3F),
    inverseSurface = Color(0xFF293429),
    inverseOnSurface = Color(0xFFEDF7ED),
    outline = Color(0xFF6E8274),
    outlineVariant = Color(0xFFC2D6C6),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFFF6FFF6),
    surfaceDim = Color(0xFFD6E0D6),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEAFBEB),
    surfaceContainer = Color(0xFFE4F7E7),
    surfaceContainerHigh = Color(0xFFD6F2DA),
    surfaceContainerHighest = Color(0xFFCBE8D0),
)

internal val WlDarkColorScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF8CE59B),
    onPrimary = Color(0xFF003912),
    primaryContainer = Color(0xFF1B5C31),
    onPrimaryContainer = Color(0xFFC4F7CD),
    inversePrimary = Color(0xFF237A3F),
    secondary = Color(0xFFB6CCBA),
    onSecondary = Color(0xFF213527),
    secondaryContainer = Color(0xFF2C4633),
    onSecondaryContainer = Color(0xFFDDF3E0),
    tertiary = Color(0xFFFFB25C),
    onTertiary = Color(0xFF3B1A00),
    tertiaryContainer = Color(0xFF5C3A00),
    onTertiaryContainer = Color(0xFFFFDCC4),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF111A13),
    onBackground = Color(0xFFE2F3E4),
    surface = Color(0xFF111A13),
    onSurface = Color(0xFFE2F3E4),
    surfaceVariant = Color(0xFF43554A),
    onSurfaceVariant = Color(0xFFBFD3C3),
    surfaceTint = Color(0xFF8CE59B),
    inverseSurface = Color(0xFFE2F3E4),
    inverseOnSurface = Color(0xFF293429),
    outline = Color(0xFF8A9C8E),
    outlineVariant = Color(0xFF43554A),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFF37423A),
    surfaceDim = Color(0xFF111A13),
    surfaceContainerLowest = Color(0xFF0C130E),
    surfaceContainerLow = Color(0xFF18231A),
    surfaceContainer = Color(0xFF1E2A20),
    surfaceContainerHigh = Color(0xFF243325),
    surfaceContainerHighest = Color(0xFF2F3F31),
)

/**
 * Colors outside the Material scheme.
 *
 * - [fill] marks a fulfilled bingo cell. Brighter than `primary`, with dark text on top.
 * - [line] marks a completed bingo line. A different hue from `primary` so it stands out.
 */
@Immutable
data class WlExtendedColors(
    val fill: Color,
    val onFill: Color,
    val line: Color,
    val onLine: Color,
)

internal val WlLightExtendedColors = WlExtendedColors(
    fill = Color(0xFF7EE28F),
    onFill = Color(0xFF003912),
    line = Color(0xFFFF9E3D),
    onLine = Color(0xFF3B1A00),
)

internal val WlDarkExtendedColors = WlExtendedColors(
    fill = Color(0xFF55D06B),
    onFill = Color(0xFF002A0C),
    line = Color(0xFFFFB25C),
    onLine = Color(0xFF3B1A00),
)

internal val LocalWlExtendedColors = staticCompositionLocalOf { WlLightExtendedColors }
