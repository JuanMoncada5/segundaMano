package com.uniquindio.segundaMano.domain.entity;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.valueObject.EstadoTransaccion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransaccionAgregadoTest {

    private UniCambista nuevoUniCambista(String correo, String nombre) {
        UniCambista u = UniCambista.registrar(correo, nombre);
        u.verificarCorreo();
        return u;
    }

    @Test
    void noDebeIniciarseTransaccionConOfertaQueNoEstaAceptada() {
        // Arrange
        UniCambista vendedor = nuevoUniCambista("vendedor@uniquindio.edu.co", "Laura");
        UniCambista comprador = nuevoUniCambista("comprador@uniquindio.edu.co", "Juan");
        Publicacion publicacion = Publicacion.publicar(vendedor, "Calculadora Casio", new BigDecimal("150000"));
        Oferta ofertaPendiente = publicacion.recibirOferta(comprador, new BigDecimal("100000"));

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> Transaccion.iniciar(ofertaPendiente));
    }

    @Test
    void noDebePasarACompletadaHastaQueAmbosConfirmen() {
        // Arrange
        UniCambista vendedor = nuevoUniCambista("vendedor@uniquindio.edu.co", "Laura");
        UniCambista comprador = nuevoUniCambista("comprador@uniquindio.edu.co", "Juan");
        Publicacion publicacion = Publicacion.publicar(vendedor, "Calculadora Casio", new BigDecimal("150000"));
        Oferta oferta = publicacion.recibirOferta(comprador, new BigDecimal("100000"));
        publicacion.aceptarOferta(oferta.getId(), vendedor);
        Transaccion transaccion = Transaccion.iniciar(oferta);

        // Act
        transaccion.confirmar(comprador);

        // Assert
        assertEquals(EstadoTransaccion.PENDIENTE, transaccion.getEstado());
        transaccion.confirmar(vendedor);
        assertEquals(EstadoTransaccion.COMPLETADA, transaccion.getEstado());
    }

    @Test
    void soloElCompradorOElVendedorPuedenConfirmarLaTransaccion() {
        // Arrange
        UniCambista vendedor = nuevoUniCambista("vendedor@uniquindio.edu.co", "Laura");
        UniCambista comprador = nuevoUniCambista("comprador@uniquindio.edu.co", "Juan");
        UniCambista tercero = nuevoUniCambista("tercero@uniquindio.edu.co", "Pedro");
        Publicacion publicacion = Publicacion.publicar(vendedor, "Calculadora Casio", new BigDecimal("150000"));
        Oferta oferta = publicacion.recibirOferta(comprador, new BigDecimal("100000"));
        publicacion.aceptarOferta(oferta.getId(), vendedor);
        Transaccion transaccion = Transaccion.iniciar(oferta);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> transaccion.confirmar(tercero));
        assertEquals(EstadoTransaccion.PENDIENTE, transaccion.getEstado());
    }

    @Test
    void elMontoAcordadoQuedaCongeladoAlIniciarLaTransaccion() {
        // Arrange
        UniCambista vendedor = nuevoUniCambista("vendedor@uniquindio.edu.co", "Laura");
        UniCambista comprador = nuevoUniCambista("comprador@uniquindio.edu.co", "Juan");
        Publicacion publicacion = Publicacion.publicar(vendedor, "Calculadora Casio", new BigDecimal("150000"));
        Oferta oferta = publicacion.recibirOferta(comprador, new BigDecimal("100000"));
        publicacion.aceptarOferta(oferta.getId(), vendedor);

        // Act
        Transaccion transaccion = Transaccion.iniciar(oferta);

        // Assert
        assertEquals(new BigDecimal("100000"), transaccion.getMontoAcordado());
    }
}