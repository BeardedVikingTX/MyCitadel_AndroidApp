package lol.mycitadel.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CitadelDarkScheme = darkColorScheme(
    background         = Void,
    surface            = Abyss,
    surfaceVariant     = Slab,
    surfaceTint        = Cyan,
    primary            = Cyan,
    onPrimary          = Void,
    primaryContainer   = CyanDim,
    onPrimaryContainer = TextPrimary,
    secondary          = Gold,
    onSecondary        = Void,
    secondaryContainer = GoldBright,
    tertiary           = Rune,
    onTertiary         = TextPrimary,
    onBackground       = TextPrimary,
    onSurface          = TextPrimary,
    onSurfaceVariant   = TextDim,
    outline            = Cyan.copy(alpha = 0.35f),
    error              = Blood,
    onError            = TextPrimary,
    errorContainer     = BloodDim,
)

@Composable
fun MyCitadelTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Void.toArgb()
            window.navigationBarColor = Void.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = CitadelTypography.let { CitadelDarkScheme },
        typography = CitadelTypography,
        content = content
    )
}