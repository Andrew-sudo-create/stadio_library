package com.example.stadio_library.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGold,
    secondary = TextGrey,
    tertiary = AvailableGreen,
    background = BackgroundGrey,
    surface = SurfaceWhite,
    onPrimary = SurfaceWhite,
    onSecondary = SurfaceWhite,
    onTertiary = SurfaceWhite,
    onBackground = TextDark,
    onSurface = TextDark,
    outline = OutlineGrey
)

@Composable
fun STADIO_LibraryTheme(
    darkTheme: Boolean = false, // Force light theme for this design
    dynamicColor: Boolean = false, // Force custom colors
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
