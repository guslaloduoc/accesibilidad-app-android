package com.example.exp1_s2_gustavo_renjifo.ui.theme

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
    primary = GreenDark40,
    secondary = GreenGreyDark40,
    tertiary = GreenAccentDark40,
    background = Color(0xFF0D1117),
    surface = Color(0xFF161B22),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onTertiary = Color(0xFF000000),
    onBackground = Color(0xFFE6EDF3),
    onSurface = Color(0xFFE6EDF3)
)

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    secondary = GreenGrey40,
    tertiary = GreenAccent40,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun Exp1_S2_gustavo_renjifoTheme(
    darkTheme: Boolean = AppPreferences.isDarkTheme,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Deshabilitado para usar nuestra paleta verde
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