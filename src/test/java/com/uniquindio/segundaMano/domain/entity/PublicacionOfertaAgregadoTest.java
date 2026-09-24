package com.uniquindio.segundaMano.domain.entity;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.valueObject.EstadoOferta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PublicacionOfertaAgregadoTest {

    @Test
    void noDebePermitirOfertaMenorAl50PorcientoYNoModificaLaPublicacion() {
        // Arrange
        UniCambista vendedor = UniCambista.registrar("vendedor@uniquindio.edu.co", "Laura");
        vendedor.verificarCorreo();
        UniCambista comprador = UniCambista.registrar("comprador@uniquindio.edu.co", "Juan");
        comprador.verificarCorreo();
        Publicacion publicacion = Publicacion.publicar(vendedor, "Calculadora Casio", new BigDecimal("150000"));

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            publicacion.recibirOferta(comprador, new BigDecimal("60000")); // 40% del precio, no alcanza el 50%
        });
        assertTrue(publicacion.getOfertas().isEmpty()); // el estado no cambió: no se agregó ninguna oferta
    }

    @Test
    void noDebePermitirAceptarOfertaSiQuienConfirmaNoEsElDueno() {
        // Arrange
        UniCambista vendedor = UniCambista.registrar("vendedor@uniquindio.edu.co", "Laura");
        vendedor.verificarCorreo();
        UniCambista comprador = UniCambista.registrar("comprador@uniquindio.edu.co", "Juan");
        comprador.verificarCorreo();
        UniCambista otroUniCambista = UniCambista.registrar("otro@uniquindio.edu.co", "Pedro");
        otroUniCambista.verificarCorreo();

        Publicacion publicacion = Publicacion.publicar(vendedor, "Calculadora Casio", new BigDecimal("150000"));
        Oferta oferta = publicacion.recibirOferta(comprador, new BigDecimal("100000")); // válida, >= 50%

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            publicacion.aceptarOferta(oferta.getId(), otroUniCambista); // no es el dueño
        });
        assertEquals(EstadoOferta.PENDIENTE, oferta.getEstado()); // el estado no cambió tras el rechazo
    }
}