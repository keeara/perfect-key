// SPDX-License-Identifier: GPL-3.0-only
package com.perfectkey.keyboard.latin.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun Theme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val material3 = Typography()
    // fixed colors, so the settings look the same on every phone: one background, boxes a little lighter (dark) or white (light)
    val accent = Color(0xFF0A84FF)
    val colorScheme = if (dark) darkColorScheme(
        primary = accent,
        background = Color(0xFF0B0D15),
        surface = Color(0xFF0B0D15),
        onBackground = Color.White,
        onSurface = Color.White,
        onSurfaceVariant = Color(0xFF8E8E93),
        surfaceContainerLowest = Color(0xFF1C2030),
        surfaceContainerLow = Color(0xFF1C2030),
        surfaceContainer = Color(0xFF1C2030),
        surfaceContainerHigh = Color(0xFF232839),
        surfaceContainerHighest = Color(0xFF2A3045),
        outlineVariant = Color(0xFF2C3042),
    ) else lightColorScheme(
        primary = accent,
        background = Color(0xFFF2F2F7),
        surface = Color(0xFFF2F2F7),
        onBackground = Color.Black,
        onSurface = Color.Black,
        onSurfaceVariant = Color(0xFF6C6C70),
        surfaceContainerLowest = Color.White,
        surfaceContainerLow = Color.White,
        surfaceContainer = Color.White,
        surfaceContainerHigh = Color.White,
        surfaceContainerHighest = Color(0xFFE5E5EA),
        outlineVariant = Color(0xFFD1D1D6),
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            titleLarge = material3.titleLarge.copy(fontWeight = FontWeight.Bold),
            titleMedium = material3.titleMedium.copy(fontWeight = FontWeight.Bold),
            titleSmall = material3.titleSmall.copy(fontWeight = FontWeight.Bold)
        ),
        //shapes = Shapes(),
        content = content
    )
}

const val previewDark = true
