package com.uniquindio.segundaMano.domain.entity;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UniCambistaCalificacionTest {

    private UniCambista nuevoUniCambista(String correo, String nombre) {
        UniCambista u = UniCambista.registrar(correo, nombre);
        u.verificarCorreo();
        return u;
    }

    private Transaccion transaccionCompletada(UniCambista vendedor, UniCambista comprador) {
        Publicacion publicacion = Publicacion.publicar(vendedor, "Calculadora Casio", new BigDecimal("150000"));
        Oferta oferta = publicacion.recibirOferta(comprador, new BigDecimal("100000"));
        publicacion.aceptarOferta(oferta.getId(), vendedor);
        Transaccion transaccion = Transaccion.iniciar(oferta);
        transaccion.confirmar(comprador);
        transaccion.confirmar(vendedor);
        return transaccion;
    }

    @Test
    void calificarActualizaLaRachaConfianzaDelOtroUniCambista() {
        // Arrange
        UniCambista vendedor = nuevoUniCambista("vendedor@uniquindio.edu.co", "Laura");
        UniCambista comprador = nuevoUniCambista("comprador@uniquindio.edu.co", "Juan");
        Transaccion transaccion = transaccionCompletada(vendedor, comprador);

        // Act
        comprador.calificar(vendedor, transaccion, 4);

        // Assert
        assertEquals(1, vendedor.getRachaConfianza().totalTransacciones());
        assertEquals(4.0, vendedor.getRachaConfianza().promedioEstrellas(), 0.001);
    }

    @Test
    void noDebePermitirCalificarSiNoSeParticipoEnLaTransaccion() {
        // Arrange
        UniCambista vendedor = nuevoUniCambista("vendedor@uniquindio.edu.co", "Laura");
        UniCambista comprador = nuevoUniCambista("comprador@uniquindio.edu.co", "Juan");
        UniCambista ajeno = nuevoUniCambista("ajeno@uniquindio.edu.co", "Pedro");
        Transaccion transaccion = transaccionCompletada(vendedor, comprador);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> ajeno.calificar(vendedor, transaccion, 5));
        assertEquals(0, vendedor.getRachaConfianza().totalTransacciones());
    }

    @Test
    void noDebePermitirCalificarUnaTransaccionQueNoEstaCompletada() {
        // Arrange
        UniCambista vendedor = nuevoUniCambista("vendedor@uniquindio.edu.co", "Laura");
        UniCambista comprador = nuevoUniCambista("comprador@uniquindio.edu.co", "Juan");
        Publicacion publicacion = Publicacion.publicar(vendedor, "Calculadora Casio", new BigDecimal("150000"));
        Oferta oferta = publicacion.recibirOferta(comprador, new BigDecimal("100000"));
        publicacion.aceptarOferta(oferta.getId(), vendedor);
        Transaccion pendiente = Transaccion.iniciar(oferta);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> comprador.calificar(vendedor, pendiente, 5));
    }

    @Test
    void noDebePermitirCalificarDosVecesLaMismaTransaccion() {
        // Arrange
        UniCambista vendedor = nuevoUniCambista("vendedor@uniquindio.edu.co", "Laura");
        UniCambista comprador = nuevoUniCambista("comprador@uniquindio.edu.co", "Juan");
        Transaccion transaccion = transaccionCompletada(vendedor, comprador);
        comprador.calificar(vendedor, transaccion, 5);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> comprador.calificar(vendedor, transaccion, 1));
        assertEquals(1, vendedor.getRachaConfianza().totalTransacciones());
        assertEquals(5.0, vendedor.getRachaConfianza().promedioEstrellas(), 0.001);
    }
}
