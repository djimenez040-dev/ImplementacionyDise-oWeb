package com.ebc.marcandohuella.model
// Guarda la placa elegida y los datos del cliente.
data class Pedido(
 val pieza: Int = -1, val mascota: String = "", val letra: Int = 0,
 val cantidad: Int = 1, val nombre: String = "", val telefono: String = "", val correo: String = ""
) {
 // Calcula el total según la cantidad de placas.
 val total: Int get() = cantidad * Catalogo.PRECIO
 // Revisa que se haya elegido una figura, un nombre y una letra válida.
 val placaValida: Boolean get() = pieza in Catalogo.piezas.indices && mascota.trim().length in 1..18 && letra in Catalogo.letras.indices
 // Revisa el nombre del cliente, el teléfono y el correo.
 val clienteValido: Boolean get() = nombre.trim().isNotEmpty() && telefono.length == 10 && android.util.Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()
 // Prepara el texto que se puede copiar o enviar por WhatsApp.
 fun resumen(): String = buildString {
  appendLine("Hola, quiero cotizar placas de Marcando Huella:")
  appendLine("Figura: ${Catalogo.piezas.getOrNull(pieza)?.nombre.orEmpty()}")
  appendLine("Nombre a grabar: ${mascota.trim()}")
  appendLine("Tipografía: ${Catalogo.letras.getOrNull(letra)?.nombre.orEmpty()}")
  appendLine("Cantidad: $cantidad")
  appendLine("Precio por pieza: $${Catalogo.PRECIO} MXN")
  appendLine("Total estimado: $$total MXN")
  appendLine("Cliente: ${nombre.trim()}")
  appendLine("Teléfono: $telefono")
  appendLine("Correo: ${correo.trim()}")
  append("¿Me ayudan a confirmar medidas, acabados, disponibilidad y costo final?")
 }
}
