package com.rmakiyama.wishline.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Material 3 type scale with heavier weights on the display, headline and title roles.
 * Body and label roles keep the Material defaults.
 */
internal val WlTypography: Typography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.5).sp,
        ),
        displayMedium = displayMedium.copy(
            fontWeight = FontWeight.Black,
        ),
        displaySmall = displaySmall.copy(
            fontSize = 36.sp,
            lineHeight = 44.sp,
            fontWeight = FontWeight.Black,
        ),
        headlineLarge = headlineLarge.copy(
            fontWeight = FontWeight.Black,
        ),
        headlineMedium = headlineMedium.copy(
            fontSize = 28.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Black,
        ),
        headlineSmall = headlineSmall.copy(
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Black,
        ),
        titleLarge = titleLarge.copy(
            fontSize = 22.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Bold,
        ),
        titleMedium = titleMedium.copy(
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Black,
        ),
        titleSmall = titleSmall.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold,
        ),
        labelLarge = labelLarge.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold,
        ),
    )
}
