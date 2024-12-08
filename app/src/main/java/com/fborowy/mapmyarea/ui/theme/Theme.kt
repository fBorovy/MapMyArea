package com.fborowy.mapmyarea.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF121212),
    secondary = Color(0xFF1F1F1F),
    tertiary = Color(0xFF424242),
    background = Color(0xFF181818),

    onPrimary = Color.White,
    onSecondary = Color(0xFFC5C5C5),
    onTertiary = Color(0xFFD9D9D9),
    onBackground = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFEAD8D7),
    secondary = Color(0xFFE3CCCA),
    tertiary = Color(0xFFB97D79),
    background = Color(0xFFF8F2F2),//onMapButtonBackground.copy(alpha = 0.85f),//Color(0xFFF8F2F2),

    onPrimary = Color(0xFF323232),
    onSecondary = Color(0xFF765856),
    onTertiary = Color(0xFF737373),
    onBackground = Color(0xFF212121)
)

@Composable
fun MapMyAreaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    //Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme//when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}