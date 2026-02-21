package de.simson.companion.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DDRColorScheme = darkColorScheme(
    primary          = DDRAmber,
    onPrimary        = DDRBackground,
    primaryContainer = DDRPanelHeader,
    secondary        = DDRGreen,
    onSecondary      = DDRDisplayBg,
    background       = DDRBackground,
    onBackground     = DDRTextPrimary,
    surface          = DDRPanel,
    onSurface        = DDRTextPrimary,
    error            = DDRRedBright,
    onError          = DDRTextPrimary,
    outline          = DDRBorderNormal,
)

@Composable
fun SimsonTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DDRColorScheme,
        typography  = DDRTypography,
        content     = content
    )
}
