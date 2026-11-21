MARCANDO HUELLA - NOTIFICACIONES Y ANIMACIONES

El proyecto se encuentra dentro de MuffinShop, pero su raíz real es:
C:\Users\Daniel1\Desktop\EBC\Dessarrollo de Apps Moviles I\MuffinShop\app\src\main\java\com\ebc\MarcandoHuella

Abre esa carpeta desde Android Studio > Open. Es la que contiene settings.gradle.kts.
Las rutas siguientes son relativas a esa carpeta. No reemplaces los archivos del proyecto exterior com.ebc.muffinshop.

ARCHIVOS NUEVOS
- Notificaciones.kt: Kotlin File en el paquete com.ebc.marcandohuella. Usa un objeto para crear el canal y publicar los avisos.
- ui/AvisosCotizacion.kt: Kotlin File en com.ebc.marcandohuella.ui. Contiene los botones y la solicitud del permiso.
- ui/Animaciones.kt: Kotlin File en com.ebc.marcandohuella.ui. Contiene el progreso móvil y el resumen animado.
- res/drawable/ic_notificacion_huella.xml: Drawable Resource File XML. Icono blanco para la barra de notificaciones.

ARCHIVOS MODIFICADOS
- MainActivity.kt: Kotlin Class. Crea el canal y abre el borrador al tocar una notificación.
- ui/MarcandoHuellaApp.kt: Kotlin File. Conecta los avisos y las animaciones al flujo de la app.
- AndroidManifest.xml: registra POST_NOTIFICATIONS y singleTop para reutilizar la actividad.
- app/build.gradle.kts: agrega androidx.compose.animation:animation con la versión del BOM existente.

No se necesitan Java Classes, Firebase, imágenes, servicios ni permisos de alarmas.
Los modelos, el ViewModel, el precio, las figuras y las fuentes se conservan.

CÓMO USAR LOS AVISOS
1. Completa los cinco pasos hasta el resumen.
2. En Avisos de mi cotización, pulsa Crear aviso sencillo o Crear aviso con detalles.
3. Desde Android 13, acepta el permiso cuando se solicite. Si lo rechazas, la app sigue funcionando y permite abrir Ajustes.
4. Abre la bandeja de notificaciones. El aviso con detalles se puede expandir.
5. Toca el aviso para volver al borrador actual. No es una copia histórica: si editaste el pedido, se abre la información más reciente.
6. Limpiar avisos elimina los dos tipos de notificación. Reiniciar la solicitud también los elimina.

Los avisos se publican en ese momento. No son alarmas programadas ni confirman que se envió un mensaje o un pedido. Repetir el mismo tipo actualiza su aviso, sin llenar la bandeja de copias.
No incluyen el teléfono ni el correo; se usa visibilidad privada para la pantalla bloqueada.

COMPATIBILIDAD
- API 24-25 Nougat: NotificationCompat publica el aviso sin canales ni permiso en tiempo de ejecución.
- API 26-32: se crea un NotificationChannel.
- API 33 o superior: se solicita POST_NOTIFICATIONS al pulsar el botón, no al abrir la app.
- Si el usuario bloqueó la app o el canal, se informa y se ofrece Abrir ajustes de notificaciones.
- El canal usa IMPORTANCE_HIGH y la prioridad alta para permitir un aviso emergente. El sistema, No molestar y la configuración del usuario pueden impedirlo.

ANIMACIONES AÑADIDAS
- animateColorAsState y tween de 300 ms: color de la figura seleccionada.
- animateContentSize: cambio de tamaño de la tarjeta de vista previa y del resumen.
- AnimatedVisibility, fadeIn/fadeOut y slideInVertically/slideOutVertically: mostrar y ocultar los detalles del resumen.
- BoxWithConstraints, maxWidth, Modifier.offset y animateDpAsState con spring: círculo turquesa que avanza con los cinco pasos. El movimiento se limita al ancho disponible.

Para probarlas, cambia de figura, avanza o regresa entre pasos y pulsa Ocultar/Mostrar detalles en el resumen.
Las cinco @Preview existentes se conservan. Los avisos se prueban con Run; Preview no abre permisos ni publica notificaciones.

COMPROBACIÓN MANUAL
- Verifica que el nombre vacío y un correo incorrecto sigan bloqueando Siguiente.
- Comprueba el total de tres placas: $330 MXN.
- Crea ambos tipos de avisos y toca cada uno con la app abierta y después de cerrarla.
- Deniega el permiso en Android 13 y comprueba que el flujo de cotización siga disponible.
- Desactiva el canal desde Ajustes y comprueba el mensaje al intentar publicar.
- Reinicia la solicitud y verifica que se quiten los avisos.
- En Nougat, verifica que se publiquen sin intentar crear un canal.

Referencias oficiales:
https://developer.android.com/develop/ui/compose/notifications/create-notification
https://developer.android.com/develop/ui/compose/notifications/notification-permission
https://developer.android.com/develop/ui/compose/animation/composables-modifiers

Los comentarios del código son breves y explican la función de cada parte, siguiendo el nivel de notificacionesdemo.

VALIDACIÓN REALIZADA
Se aplicaron y verificaron 8 archivos en el proyecto original. Los archivos anteriores están en Respaldo_MarcandoHuella_antes_de_avisos.zip.
Gradle assembleDebug: correcto.
Gradle testDebugUnitTest: 4 pruebas, 0 fallos y 0 errores.
Emulador Android 11 API 30: aviso sencillo y expandible publicados; comprobado total de $330 MXN; tocar el aviso abre el resumen; navegación y mostrar/ocultar detalles correctos; limpiar avisos retira los avisos activos.
Android Lint: no se completó porque falla la validación del certificado SSL al descargar lint-gradle:32.4.1. No es un error de compilación del código.
Pendiente: ejecutar en API 24 Nougat y comprobar aceptación/denegación del permiso en API 33 o superior.
