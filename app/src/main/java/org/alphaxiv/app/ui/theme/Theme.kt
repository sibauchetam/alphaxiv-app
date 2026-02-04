package org.alphaxiv.app.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AlphaXivRed,
    secondary = AlphaXivRed.copy(alpha = 0.7f),
    tertiary = AlphaXivRed.copy(alpha = 0.5f)
)

private val DarkColorScheme = darkColorScheme(
    primary = AlphaXivRed,
    secondary = AlphaXivRed.copy(alpha = 0.7f),
    tertiary = AlphaXivRed.copy(alpha = 0.5f)
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AlphaXivTheme(
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity ?: (view.context as? ContextWrapper)?.baseContext as? Activity)?.window
            window?.let {
                it.statusBarColor = colorScheme.surface.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}
