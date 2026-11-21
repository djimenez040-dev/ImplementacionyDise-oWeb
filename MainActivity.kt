package com.ebc.marcandohuella

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.ebc.marcandohuella.ui.MarcandoHuellaApp
import com.ebc.marcandohuella.ui.theme.MarcandoHuellaTheme
import com.ebc.marcandohuella.viewmodel.PedidoViewModel

class MainActivity : ComponentActivity() {
    private val pedidoViewModel: PedidoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Registra el canal una sola vez; no pide permiso al abrir la app.
        Notificaciones.crearCanal(this)
        if (savedInstanceState == null) revisarAviso(intent)
        setContent {
            MarcandoHuellaTheme { MarcandoHuellaApp(pedidoViewModel) }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        revisarAviso(intent)
    }

    private fun revisarAviso(intent: Intent) {
        if (intent.getBooleanExtra(Notificaciones.ABRIR_COTIZACION, false)) {
            // Revisa el borrador actual, sin recuperar una copia vieja del pedido.
            val p = pedidoViewModel.pedido
            pedidoViewModel.ir(if (p.placaValida && p.clienteValido) 4 else 0)
            intent.removeExtra(Notificaciones.ABRIR_COTIZACION)
        }
    }
}
