package com.example.vpntestapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark VPN-style color palette
val DarkBackground = Color(0xFF0A0E1A)
val DarkSurface = Color(0xFF111827)
val DarkCard = Color(0xFF1C2333)
val AccentCyan = Color(0xFF00D4FF)
val AccentGreen = Color(0xFF00E676)
val AccentOrange = Color(0xFFFF6B35)
val TextPrimary = Color(0xFFE8EAF0)
val TextSecondary = Color(0xFF8A95A3)
val ConnectedColor = Color(0xFF00E676)
val ConnectingColor = Color(0xFFFFB300)
val DisconnectedColor = Color(0xFF546E7A)
val ErrorColor = Color(0xFFFF5252)

private val DarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = Color(0xFF001F2A),
    primaryContainer = Color(0xFF00344A),
    onPrimaryContainer = AccentCyan,
    secondary = AccentGreen,
    onSecondary = Color(0xFF00210A),
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondary,
    error = ErrorColor,
    onError = Color.White,
    outline = Color(0xFF2A3444),
)

@Composable
fun VPNTestAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
