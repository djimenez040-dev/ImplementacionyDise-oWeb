package com.ebc.marcandohuella.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.ebc.marcandohuella.Notificaciones
import com.ebc.marcandohuella.model.Pedido

@Composable
fun AvisosCotizacion(pedido: Pedido) {
    val context = LocalContext.current
    val preview = LocalInspectionMode.current
    // La vista previa no abre permisos ni necesita una actividad real.
    if (preview) {
        Card(Modifier.fillMaxWidth()) {
            Text("Avisos de mi cotización: pruébalos con Run en un dispositivo.", Modifier.padding(14.dp))
        }
        return
    }
    var tipoPendiente by rememberSaveable { mutableStateOf(0) }
    var mostrarAjustes by rememberSaveable { mutableStateOf(false) }
    val datosValidos = pedido.placaValida && pedido.clienteValido

    // El tipo 1 es un aviso sencillo y el 2 lleva texto expandible.
    fun publicar(tipo: Int) {
        val enviado = if (tipo == 1) Notificaciones.enviarBasica(context)
            else Notificaciones.enviarDetalle(context, pedido)
        mostrarAjustes = !enviado
        Toast.makeText(context, if (enviado) "Aviso publicado en notificaciones"
            else "Los avisos están desactivados. Puedes habilitarlos en Ajustes.", Toast.LENGTH_LONG).show()
    }

    // Espera la respuesta del permiso antes de publicar el aviso elegido.
    val permiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { aceptado ->
        val tipo = tipoPendiente
        tipoPendiente = 0
        if (aceptado && tipo != 0 && datosValidos) publicar(tipo)
        else if (!aceptado) {
            mostrarAjustes = true
            Toast.makeText(context, "Puedes seguir usando la app sin notificaciones.", Toast.LENGTH_LONG).show()
        }
    }

    fun solicitarAviso(tipo: Int) {
        if (preview || !datosValidos) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            tipoPendiente = tipo
            permiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else publicar(tipo)
    }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Avisos de mi cotización", style = MaterialTheme.typography.titleMedium)
            Text("Publica un recordatorio ahora para volver al borrador desde las notificaciones.")
            OutlinedButton(onClick = { solicitarAviso(1) }, enabled = !preview && datosValidos) { Text("Crear aviso sencillo") }
            OutlinedButton(onClick = { solicitarAviso(2) }, enabled = !preview && datosValidos) { Text("Crear aviso con detalles") }
            TextButton(onClick = { Notificaciones.limpiar(context) }, enabled = !preview) { Text("Limpiar avisos") }
            if (mostrarAjustes) {
                TextButton(onClick = {
                    // Abre los ajustes de notificaciones de esta app.
                    val ajustes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    else Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.parse("package:${context.packageName}"))
                    context.startActivity(ajustes)
                }) { Text("Abrir ajustes de notificaciones") }
            }
        }
    }
}
