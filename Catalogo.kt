package com.ebc.marcandohuella.model
import com.ebc.marcandohuella.R
// Guarda los datos de cada figura.
data class Pieza(val id: String, val nombre: String, val categoria: String)
// Relaciona el nombre de la tipografía con su archivo de fuente.
data class Letra(val nombre: String, val recurso: Int)
object Catalogo {
 // Todas las placas tienen el mismo precio por pieza.
 const val PRECIO = 110
 // Lista de las figuras que se pueden elegir.
 val piezas = listOf(
  Pieza("redondo-chico-corazon", "Redondo chico con corazón troquelado", "troquelada"),
  Pieza("hueso-largo-huella", "Hueso largo con huella troquelada", "troquelada"),
  Pieza("rombo", "Rombo", "clasica"),
  Pieza("estrella", "Estrella", "clasica"),
  Pieza("hueso-grande-huella", "Hueso grande con huella troquelada", "troquelada"),
  Pieza("hueso-grande", "Hueso grande", "clasica"),
  Pieza("ovalo", "Óvalo", "clasica"),
  Pieza("ovalo-corazon", "Óvalo con corazón troquelado", "troquelada"),
  Pieza("hueso-largo", "Hueso largo", "clasica"),
  Pieza("hueso-chico", "Hueso chico", "clasica"),
  Pieza("corazon-mediano", "Corazón mediano", "clasica"),
  Pieza("corazon-chico", "Corazón chico", "clasica"),
  Pieza("redondo-grande", "Redondo grande", "clasica"),
  Pieza("redondo-mediano", "Redondo mediano", "clasica"),
  Pieza("pez", "Pez", "animal"),
  Pieza("gato-bigotes", "Gato con bigotes", "animal"),
  Pieza("gato-sin-bigotes", "Gato sin bigotes", "animal")
 )
 // Lista de las 12 tipografías para el nombre de la mascota.
 val letras = listOf(
  Letra("Alex Brush", R.font.alex_brush),
  Letra("Oregano", R.font.oregano),
  Letra("Gloria Hallelujah", R.font.gloria_hallelujah),
  Letra("Shadows Into Light", R.font.shadows_into_light),
  Letra("Allura", R.font.allura),
  Letra("Antonio", R.font.antonio),
  Letra("Caveat", R.font.caveat),
  Letra("Mogra", R.font.mogra),
  Letra("Sacramento", R.font.sacramento),
  Letra("Montserrat", R.font.montserrat),
  Letra("Neucha", R.font.neucha),
  Letra("Euphoria Script", R.font.euphoria_script)
 )
}
