package com.ebc.marcandohuella.ui.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
// Define los colores que se tomaron del sitio web.
private val Colores = darkColorScheme(
 primary=Color(0xFF0EE1E5), onPrimary=Color(0xFF070B0D),
 background=Color(0xFF070B0D), onBackground=Color(0xFFF7FAF9),
 surface=Color(0xFF101719), onSurface=Color(0xFFF7FAF9),
 surfaceVariant=Color(0xFF172124), onSurfaceVariant=Color(0xFFAEBCBC),
 secondary=Color(0xFF609DF3)
)
// Aplica los mismos colores a todas las pantallas.
@Composable fun MarcandoHuellaTheme(content: @Composable () -> Unit) {
 MaterialTheme(colorScheme=Colores, content=content)
}
