package com.example.omni.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    background = Color(0xFF070B14),
    surface = Color(0xFF101827),
    surfaceVariant = Color(0xFF172033),
    primary = Color(0xFF8B5CF6),
    secondary = Color(0xFF3B82F6),
    tertiary = Color(0xFF22C55E),
    onBackground = Color(0xFFEAF2FF),
    onSurface = Color(0xFFEAF2FF),
    onSurfaceVariant = Color(0xFF9FB3D1),
    outline = Color(0xFF2A3A52)
)

private val LightColorScheme = lightColorScheme(
    background = Color(0xFFF4F7FF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE9EEF9),
    primary = Color(0xFF6D4AE8),
    secondary = Color(0xFF2563EB),
    tertiary = Color(0xFF16A34A),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFDDE7F5)
)

@Composable
fun OmniTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}