package com.example.ecare_mobile.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Primary400,
    secondary = Secondary400,
    tertiary = Secondary300,
    error = Error600,
    background = Gray900,
    surface = Gray800,
    onPrimary = White,
    onSecondary = Gray900,
    onTertiary = Gray900,
    onError = White,
    onBackground = Gray100,
    onSurface = Gray100,
    surfaceVariant = Gray700,
    onSurfaceVariant = Gray300,
    outline = Gray600,
    outlineVariant = Gray500
)

private val LightColorScheme = lightColorScheme(
    primary = Primary500,
    secondary = Secondary500,
    tertiary = Secondary400,
    error = Error600,
    background = White,
    surface = Gray50,
    onPrimary = White,
    onSecondary = Gray900,
    onTertiary = Gray900,
    onError = White,
    onBackground = Gray900,
    onSurface = Gray900,
    surfaceVariant = Gray200,
    onSurfaceVariant = Gray700,
    outline = Gray400,
    outlineVariant = Gray300
)

@Composable
fun ECareMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
