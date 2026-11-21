package com.ebc.marcandohuella.ui

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import com.ebc.marcandohuella.Notificaciones
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.ebc.marcandohuella.model.Pedido
import com.ebc.marcandohuella.ui.theme.MarcandoHuellaTheme
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ebc.marcandohuella.model.Catalogo
import com.ebc.marcandohuella.viewmodel.PedidoViewModel

// Vacío conserva el comportamiento del sitio: el usuario elige el destinatario.
// Para dirigir al negocio: código de país y número, sin + ni espacios.
private const val WHATSAPP_NEGOCIO = ""

@Composable
// Dibuja una tarjeta que se puede seleccionar.
private fun Opcion(titulo: String, detalle: String, seleccionada: Boolean, click: () -> Unit) {
    // Cambia poco a poco el color de la figura seleccionada.
    val colorTarjeta by animateColorAsState(
        targetValue = if (seleccionada) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
            else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(durationMillis = 300), label = "Color de selección"
    )
    Card(
        colors = CardDefaults.cardColors(containerColor = colorTarjeta),
        onClick = click,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(if (seleccionada) 2.dp else 1.dp,
            if (seleccionada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
    ) {
        Row(Modifier.padding(12.dp)) {
            RadioButton(selected = seleccionada, onClick = null)
            Column(Modifier.padding(start = 8.dp).weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(detalle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Conecta los datos del ViewModel con las pantallas.
fun MarcandoHuellaApp(vm: PedidoViewModel = viewModel()) {
    MarcandoHuellaContenido(
        p = vm.pedido,
        paso = vm.paso,
        actualizar = vm::actualizar,
        ir = vm::ir,
        reiniciar = vm::reiniciar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Muestra el contenido y recibe las acciones para cambiar los datos.
private fun MarcandoHuellaContenido(
    p: Pedido,
    paso: Int,
    actualizar: (Pedido) -> Unit,
    ir: (Int) -> Unit,
    reiniciar: () -> Unit
) {
    val context = LocalContext.current
    val enPreview = LocalInspectionMode.current
    val titulos = listOf("Elige una figura", "Personaliza la placa", "Cantidad", "Datos del cliente", "Revisa tu solicitud")
    // Controla si se muestra la pregunta para reiniciar.
    var cancelar by rememberSaveable { mutableStateOf(false) }
    // Guarda el texto de búsqueda y la categoría seleccionada.
    var busqueda by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("Todas") }
    val categorias = listOf("Todas", "clasica", "troquelada", "animal")
    // Activa el botón de regresar del celular cuando la app está en ejecución.
    if (!LocalInspectionMode.current) {
        BackHandler(enabled = paso > 0) { ir(paso - 1) }
    }
    // Pide confirmación antes de borrar la solicitud.
    if (cancelar) {
        AlertDialog(
            onDismissRequest = { cancelar = false },
            title = { Text("¿Reiniciar solicitud?") },
            text = { Text("Se borrarán la selección y los datos guardados en este dispositivo.") },
            confirmButton = { TextButton(onClick = {
                reiniciar(); busqueda = ""; categoria = "Todas"; cancelar = false
                // Quita los avisos para que no queden cotizaciones anteriores.
                if (!enPreview) Notificaciones.limpiar(context)
            }) { Text("Sí, reiniciar") } },
            dismissButton = { TextButton(onClick = { cancelar = false }) { Text("Continuar") } }
        )
    }
    // Decide si se puede continuar al siguiente paso.
    val valido = when (paso) {
        0 -> p.pieza in Catalogo.piezas.indices
        1 -> p.placaValida
        2 -> p.cantidad in 1..99
        3, 4 -> p.placaValida && p.clienteValido
        else -> false
    }
    // Organiza la barra superior, los botones y el contenido.
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Marcando Huella", fontSize = 20.sp) },
                actions = { TextButton(onClick = { cancelar = true }) { Text("Reiniciar") } })
        },
        // Muestra el total y los botones para regresar o continuar.
        bottomBar = {
            Surface {
                Column(Modifier.fillMaxWidth().navigationBarsPadding().imePadding().padding(12.dp)) {
                    Text("Total estimado: $${p.total} MXN", color = MaterialTheme.colorScheme.primary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { ir(paso - 1) }, enabled = paso > 0, modifier = Modifier.weight(1f)) {
                            Text("Regresar")
                        }
                        Button(onClick = {
                            if (paso < 4) ir(paso + 1)
                            else abrirWhatsApp(context, p.resumen())
                        }, enabled = valido, modifier = Modifier.weight(1f)) {
                            Text(if (paso == 4) "WhatsApp" else "Siguiente")
                        }
                    }
                }
            }
        }
    ) { padding ->
        // Vuelve al inicio del contenido sin reiniciar la animación de progreso.
        val desplazamiento = rememberScrollState()
        LaunchedEffect(paso) { desplazamiento.scrollTo(0) }
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier.fillMaxSize().padding(padding).verticalScroll(desplazamiento).padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProgresoAnimado(paso)
                Text("Paso ${paso + 1} de 5", color = MaterialTheme.colorScheme.primary)
                Text(titulos[paso], style = MaterialTheme.typography.headlineSmall)
                when (paso) {
                    // Primer paso: buscar y seleccionar una figura.
                    0 -> {
                        Text("Placas de acero inoxidable con grabado láser. Precio del catálogo web: $110 MXN por pieza.")
                        OutlinedTextField(value = busqueda, onValueChange = { busqueda = it },
                            label = { Text("Buscar figura") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Text("Categoría")
                        Column {
                            categorias.forEach { cat ->
                                TextButton(onClick = { categoria = cat }) {
                                    Text((if (categoria == cat) "● " else "○ ") + when (cat) {
                                        "clasica" -> "Clásicas"
                                        "troquelada" -> "Con troquelado"
                                        "animal" -> "Figuras de animal"
                                        else -> "Todas"
                                    })
                                }
                            }
                        }
                        // Filtra las figuras según el texto y la categoría.
                        val visibles = Catalogo.piezas.withIndex().filter { (_, pieza) ->
                            pieza.nombre.contains(busqueda.trim(), ignoreCase = true) &&
                                (categoria == "Todas" || pieza.categoria == categoria)
                        }
                        Text("${visibles.size} figuras disponibles")
                        if (visibles.isEmpty()) {
                            Text("No encontramos figuras.")
                            TextButton(onClick = { busqueda = ""; categoria = "Todas" }) { Text("Limpiar filtros") }
                        }
                        visibles.forEach { (i, pieza) ->
                            Opcion(pieza.nombre, "$110 MXN · ${pieza.categoria} · Sin fotografía",
                                p.pieza == i) { actualizar(p.copy(pieza = i)) }
                        }
                    }
                    // Segundo paso: escribir el nombre y elegir la tipografía.
                    1 -> {
                        Text(Catalogo.piezas.getOrNull(p.pieza)?.nombre ?: "Selecciona primero una figura")
                        OutlinedTextField(value = p.mascota, onValueChange = {
                            actualizar(p.copy(mascota = it.replace("\n", "").replace("\r", "").take(18)))
                        }, label = { Text("Nombre de la mascota") }, supportingText = {
                            Text(if (p.mascota.isBlank()) "Escribe un nombre para continuar. Máximo 18 caracteres." else "${p.mascota.length}/18 caracteres")
                        }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        Card(Modifier.fillMaxWidth().animateContentSize(animationSpec = tween(300))) {
                            Column(Modifier.padding(20.dp)) {
                                Text("Vista previa del nombre")
                                Text(p.mascota.ifBlank { "Tu mascota" }, fontSize = 32.sp,
                                    fontFamily = FontFamily(Font(Catalogo.letras[p.letra.coerceIn(Catalogo.letras.indices)].recurso)),
                                    color = MaterialTheme.colorScheme.primary)
                                Text("Referencia de letra; no representa el tamaño o grabado final.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Text("Selecciona una tipografía")
                        Catalogo.letras.forEachIndexed { i, letra ->
                            Card(onClick = { actualizar(p.copy(letra = i)) }, modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.dp, if (p.letra == i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)) {
                                Column(Modifier.padding(14.dp)) {
                                    Text((if (p.letra == i) "● " else "○ ") + letra.nombre)
                                    Text(p.mascota.ifBlank { "Luna" }, fontSize = 26.sp, fontFamily = FontFamily(Font(letra.recurso)))
                                }
                            }
                        }
                    }
                    // Tercer paso: elegir cuántas placas se van a solicitar.
                    2 -> {
                        Text("Todas las piezas de esta solicitud tendrán la misma figura, nombre y tipografía.")
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedButton(onClick = { actualizar(p.copy(cantidad = p.cantidad - 1)) }, enabled = p.cantidad > 1) { Text("−") }
                            Text("${p.cantidad}", Modifier.padding(top = 12.dp), fontSize = 24.sp)
                            Button(onClick = { actualizar(p.copy(cantidad = p.cantidad + 1)) }, enabled = p.cantidad < 99) { Text("+") }
                        }
                        Text("De 1 a 99 piezas. Precio unitario: $110 MXN.")
                        Text("Subtotal estimado: $${p.total} MXN", style = MaterialTheme.typography.titleLarge)
                    }
                    // Cuarto paso: capturar los datos de contacto del cliente.
                    3 -> {
                        Text("Estos datos son para la consulta al negocio; no se graban en la placa.")
                        OutlinedTextField(p.nombre, { actualizar(p.copy(nombre = it.take(80))) },
                            label = { Text("Nombre del cliente") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                            supportingText = { Text("Obligatorio") })
                        OutlinedTextField(p.telefono, { actualizar(p.copy(telefono = it.filter { c -> c in '0'..'9' }.take(10))) },
                            label = { Text("Teléfono de contacto") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            supportingText = { Text("10 dígitos para un número de México") })
                        OutlinedTextField(p.correo, { actualizar(p.copy(correo = it.take(120))) },
                            label = { Text("Correo electrónico") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            supportingText = { Text("Escribe un correo válido para continuar") })
                        Text("El borrador se guarda en este dispositivo. Reiniciar elimina los datos de la solicitud.", style = MaterialTheme.typography.bodySmall)
                    }
                    // Último paso: revisar la información antes de abrir WhatsApp.
                    4 -> {
                        ResumenAnimado(p.resumen())
                        AvisosCotizacion(p)
                        Text("Se abrirá WhatsApp para revisar y enviar el mensaje. Abrirlo no confirma un pedido ni un pago.")
                        if (WHATSAPP_NEGOCIO.isBlank()) Text("No se ha configurado el número del negocio: elige el chat correcto en WhatsApp.")
                        OutlinedButton(onClick = {
                            // Copia el resumen para poder pegarlo en otra aplicación.
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Cotización Marcando Huella", p.resumen()))
                            Toast.makeText(context, "Resumen copiado", Toast.LENGTH_SHORT).show()
                        }) { Text("Copiar resumen") }
                        Text("Usa Regresar para corregir los datos. El borrador se conserva al volver de WhatsApp.")
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// Abre el enlace con el mensaje preparado para WhatsApp.
private fun abrirWhatsApp(context: Context, resumen: String) {
    val uri = Uri.parse("https://wa.me/${WHATSAPP_NEGOCIO}?text=${Uri.encode(resumen)}")
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    // Avisa si no hay una aplicación que pueda abrir el enlace.
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "No hay una app disponible. Usa Copiar resumen.", Toast.LENGTH_LONG).show()
    }
}


// Datos de ejemplo: las previews no crean ViewModels ni abren SharedPreferences.
@Preview(name = "1 Figura", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun FiguraPreview() = PreviewPaso(0)

@Preview(name = "2 Personalización", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PersonalizacionPreview() = PreviewPaso(1)

@Preview(name = "3 Cantidad", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CantidadPreview() = PreviewPaso(2)

@Preview(name = "4 Cliente", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ClientePreview() = PreviewPaso(3)

@Preview(name = "5 Resumen", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ResumenPreview() = PreviewPaso(4)

@Composable
// Usa datos de ejemplo para ver las pantallas en Android Studio.
private fun PreviewPaso(pasoInicial: Int) {
    var pedido by remember {
        mutableStateOf(Pedido(
            pieza = 13, mascota = "Luna", letra = 9, cantidad = 3,
            nombre = "Daniel", telefono = "5512345678", correo = "daniel@example.com"
        ))
    }
    var paso by remember { mutableStateOf(pasoInicial) }
    MarcandoHuellaTheme {
        MarcandoHuellaContenido(
            p = pedido, paso = paso,
            actualizar = { pedido = it },
            ir = { paso = it.coerceIn(0, 4) },
            reiniciar = { pedido = Pedido(); paso = 0 }
        )
    }
}
