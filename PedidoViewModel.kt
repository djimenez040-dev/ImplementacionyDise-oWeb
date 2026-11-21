package com.ebc.marcandohuella.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.ebc.marcandohuella.model.Pedido
import org.json.JSONObject
// Conserva los datos mientras el usuario cambia de pantalla.
class PedidoViewModel(app: Application) : AndroidViewModel(app) {
 // Usa un archivo privado para guardar el borrador en el dispositivo.
 private val prefs = app.getSharedPreferences("borrador", 0)
 // Carga el pedido guardado o crea uno vacío.
 var pedido by mutableStateOf(leer()); private set
 // Indica cuál de los cinco pasos se está mostrando.
 var paso by mutableStateOf(0); private set
 // Recupera el borrador. Si no se puede leer, usa un pedido vacío.
 private fun leer(): Pedido = runCatching {
  val j = JSONObject(prefs.getString("pedido", "{}") ?: "{}")
  Pedido(j.optInt("pieza", -1), j.optString("mascota"), j.optInt("letra", 0), j.optInt("cantidad", 1).coerceIn(1,99), j.optString("nombre"), j.optString("telefono"), j.optString("correo"))
 }.getOrDefault(Pedido())
 // Actualiza la pantalla y guarda los cambios del pedido.
 fun actualizar(p: Pedido) {
  pedido=p
  val j=JSONObject().put("pieza",p.pieza).put("mascota",p.mascota).put("letra",p.letra).put("cantidad",p.cantidad).put("nombre",p.nombre).put("telefono",p.telefono).put("correo",p.correo)
  prefs.edit().putString("pedido",j.toString()).apply()
 }
 // Cambia de paso sin salir del rango de pantallas.
 fun ir(p: Int) { paso=p.coerceIn(0,4) }
 // Borra los datos del pedido y vuelve al primer paso.
 fun reiniciar() { actualizar(Pedido()); paso=0 }
}
