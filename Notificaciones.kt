package com.ebc.marcandohuella

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.ebc.marcandohuella.model.Catalogo
import com.ebc.marcandohuella.model.Pedido

// Maneja los avisos locales de la app, como en la práctica de clase.
object Notificaciones {
    private const val CANAL_ID = "cotizaciones_marcando_huella"
    private const val AVISO_BASICO = 101
    private const val AVISO_DETALLE = 102
    const val ABRIR_COTIZACION = "abrir_cotizacion"

    fun crearCanal(context: Context) {
        // Los canales existen desde Android 8. En Nougat no se crean.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID, "Cotizaciones", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Avisos para volver a revisar tu cotización de placas"
            }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }
    }

    fun permitidas(context: Context): Boolean {
        // El permiso POST_NOTIFICATIONS solo se revisa desde Android 13.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return false
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = context.getSystemService(NotificationManager::class.java).getNotificationChannel(CANAL_ID)
            if (canal?.importance == NotificationManager.IMPORTANCE_NONE) return false
        }
        return true
    }

    private fun plantilla(context: Context, titulo: String, mensaje: String): NotificationCompat.Builder {
        // Al tocar el aviso se abre el borrador actual de la cotización.
        val intent = Intent(context, MainActivity::class.java)
            .putExtra(ABRIR_COTIZACION, true)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendiente = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.ic_notificacion_huella)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setContentIntent(pendiente)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setAutoCancel(true)
    }

    @SuppressLint("MissingPermission")
    private fun publicar(context: Context, id: Int, aviso: NotificationCompat.Builder): Boolean {
        crearCanal(context)
        // Si el usuario bloqueó los avisos, no se intenta publicar.
        if (!permitidas(context)) return false
        return try {
            NotificationManagerCompat.from(context).notify(id, aviso.build())
            true
        } catch (_: SecurityException) {
            // El permiso pudo cambiar antes de mostrar la notificación.
            false
        }
    }

    fun enviarBasica(context: Context): Boolean = publicar(
        context, AVISO_BASICO,
        plantilla(context, "Tu cotización está lista para revisar", "Toca para abrir el borrador. Aún no se ha enviado al negocio.")
    )

    fun enviarDetalle(context: Context, pedido: Pedido): Boolean {
        if (!pedido.placaValida || !pedido.clienteValido) return false
        // El aviso muestra el diseño, pero no incluye el teléfono ni el correo.
        val texto = "Figura: ${Catalogo.piezas[pedido.pieza].nombre}\n" +
            "Nombre a grabar: ${pedido.mascota.trim()}\n" +
            "Tipografía: ${Catalogo.letras[pedido.letra].nombre}\n" +
            "Cantidad: ${pedido.cantidad}\nTotal estimado: $${pedido.total} MXN\n" +
            "Toca para revisar el borrador actual. El pedido aún no está confirmado."
        return publicar(context, AVISO_DETALLE,
            plantilla(context, "Revisa tu diseño de placa", "Despliega el aviso para ver los detalles.")
                .setStyle(NotificationCompat.BigTextStyle().bigText(texto)))
    }

    fun limpiar(context: Context) {
        // Solo borra los dos avisos que usa esta app.
        NotificationManagerCompat.from(context).cancel(AVISO_BASICO)
        NotificationManagerCompat.from(context).cancel(AVISO_DETALLE)
    }
}
