package com.ebc.marcandohuella.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun ProgresoAnimado(paso: Int) {
    // El círculo avanza dentro del ancho disponible, como en el demo.
    BoxWithConstraints(Modifier.fillMaxWidth().height(32.dp)
        .semantics { contentDescription = "Paso ${paso + 1} de 5" }) {
        val diametro = 24.dp
        val limite = (maxWidth - diametro).coerceAtLeast(0.dp)
        val posicionX by animateDpAsState(
            targetValue = limite * (paso.coerceIn(0, 4) / 4f),
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            label = "Avance de la cotización"
        )
        Box(Modifier.fillMaxWidth().height(4.dp).align(Alignment.CenterStart)
            .background(MaterialTheme.colorScheme.surfaceVariant))
        Box(Modifier.offset(x = posicionX.coerceIn(0.dp, limite)).size(diametro).align(Alignment.CenterStart)
            .background(MaterialTheme.colorScheme.primary, CircleShape))
    }
}

@Composable
fun ResumenAnimado(resumen: String) {
    var visible by rememberSaveable { mutableStateOf(true) }
    // La tarjeta ajusta su tamaño al mostrar u ocultar los detalles.
    Card(Modifier.fillMaxWidth().animateContentSize(animationSpec = tween(durationMillis = 300))) {
        Column(Modifier.padding(14.dp)) {
            TextButton(onClick = { visible = !visible }) {
                Text(if (visible) "Ocultar detalles de la cotización" else "Mostrar detalles de la cotización")
            }
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(300)) + slideInVertically(initialOffsetY = { -it / 4 }),
                exit = fadeOut(tween(200)) + slideOutVertically(targetOffsetY = { -it / 4 })
            ) { Text(resumen, style = MaterialTheme.typography.bodyLarge) }
        }
    }
}
