package com.ebc.marcandohuella

import com.ebc.marcandohuella.model.Catalogo
import com.ebc.marcandohuella.model.Pedido
import org.junit.Assert.*
import org.junit.Test

class PedidoTest {
    @Test fun catalogoConservaFigurasPrecioYFuentes() {
        assertEquals(17, Catalogo.piezas.size)
        assertEquals(12, Catalogo.letras.size)
        assertEquals(110, Catalogo.PRECIO)
        assertEquals(17, Catalogo.piezas.map { it.id }.distinct().size)
    }
    @Test fun tresPiezasCuestan330() {
        assertEquals(330, Pedido(cantidad = 3).total)
    }
    @Test fun placaRequiereFiguraNombreYLetraValida() {
        assertFalse(Pedido().placaValida)
        assertFalse(Pedido(pieza = 0, mascota = "  ").placaValida)
        assertFalse(Pedido(pieza = 0, mascota = "a".repeat(19)).placaValida)
        assertFalse(Pedido(pieza = 17, mascota = "Luna").placaValida)
        assertFalse(Pedido(pieza = 0, mascota = "Luna", letra = 12).placaValida)
        assertTrue(Pedido(pieza = 0, mascota = "Luna", letra = 0).placaValida)
    }
    @Test fun resumenConservaDisenoCantidadYDatosSinAfirmarCompra() {
        val p = Pedido(pieza = 13, mascota = "Luna", letra = 9, cantidad = 3,
            nombre = "Daniel", telefono = "5512345678", correo = "daniel@example.com")
        val resumen = p.resumen()
        listOf("Redondo mediano", "Luna", "Montserrat", "Cantidad: 3", "330 MXN", "Daniel", "5512345678", "daniel@example.com")
            .forEach { assertTrue(it, resumen.contains(it)) }
        assertFalse(resumen.contains("Pedido confirmado"))
    }
}
