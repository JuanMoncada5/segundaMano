package com.uniquindio.segundaMano.domain.entity;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.valueObject.EstadoOferta;
import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Invariantes del Agregado Publicacion relacionadas con el ciclo de vida de la Oferta
 * (aceptar, rechazar, contraofertar) y con la creación/eliminación de la Publicacion.
 */
class PublicacionInvariantesTest {

    private UniCambista vendedor;
    private UniCambista comprador;
    private UniCambista otro;
    private Publicacion publicacion;

    @BeforeEach
    void preparar() {
        vendedor = UniCambista.registrar("vendedor@uniquindio.edu.co", "Laura");
        vendedor.verificarCorreo();
        comprador = UniCambista.registrar("comprador@uniquindio.edu.co", "Juan");
        comprador.verificarCorreo();
        otro = UniCambista.registrar("otro@uniquindio.edu.co", "Pedro");
        otro.verificarCorreo();
        publicacion = Publicacion.publicar(vendedor, "Calculadora Casio", new BigDecimal("150000"));
    }

    @Test
    void noDebePermitirAceptarUnaOfertaYaRechazada() {
        // Arrange
        Oferta oferta = publicacion.recibirOferta(comprador, new BigDecimal("100000"));
        publicacion.rechazarOferta(oferta.getId(), vendedor);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> publicacion.aceptarOferta(oferta.getId(), vendedor));
        assertEquals(EstadoOferta.RECHAZADA, oferta.getEstado());
        assertEquals(EstadoPublicacion.ACTIVA, publicacion.getEstado());
    }

    @Test
    void soloElDuenoPuedeContraofertar() {
        // Arrange
        Oferta oferta = publicacion.recibirOferta(comprador, new BigDecimal("100000"));

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> publicacion.contraofertar(oferta.getId(), otro, new BigDecimal("120000")));
        assertEquals(EstadoOferta.PENDIENTE, oferta.getEstado());
        assertEquals(new BigDecimal("100000"), oferta.getMontoPropuesto());
    }

    @Test
    void elCompradorAceptaLaContraofertaYLaPublicacionPasaAEnProceso() {
        // Arrange
        Oferta oferta = publicacion.recibirOferta(comprador, new BigDecimal("100000"));
        publicacion.contraofertar(oferta.getId(), vendedor, new BigDecimal("130000"));

        // Act
        publicacion.aceptarContraoferta(oferta.getId(), comprador);

        // Assert
        assertEquals(EstadoOferta.ACEPTADA, oferta.getEstado());
        assertEquals(new BigDecimal("130000"), oferta.getMontoPropuesto());
        assertEquals(EstadoPublicacion.EN_PROCESO, publicacion.getEstado());
    }

    @Test
    void elVendedorNoPuedeAceptarSuPropiaContraoferta() {
        // Arrange
        Oferta oferta = publicacion.recibirOferta(comprador, new BigDecimal("100000"));
        publicacion.contraofertar(oferta.getId(), vendedor, new BigDecimal("130000"));

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> publicacion.aceptarOferta(oferta.getId(), vendedor));
        assertThrows(ReglaDominioException.class, () -> publicacion.aceptarContraoferta(oferta.getId(), vendedor));
        assertEquals(EstadoPublicacion.ACTIVA, publicacion.getEstado());
    }

    @Test
    void noDebePermitirAceptarUnaSegundaOfertaCuandoYaEstaEnProceso() {
        // Arrange
        Oferta primera = publicacion.recibirOferta(comprador, new BigDecimal("100000"));
        Oferta segunda = publicacion.recibirOferta(otro, new BigDecimal("110000"));
        publicacion.aceptarOferta(primera.getId(), vendedor);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> publicacion.aceptarOferta(segunda.getId(), vendedor));
        assertEquals(EstadoOferta.PENDIENTE, segunda.getEstado());
    }

    @Test
    void noDebePermitirPublicarConValorNoPositivoOTituloVacio() {
        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> Publicacion.publicar(vendedor, "Libro", new BigDecimal("-5000")));
        assertThrows(ReglaDominioException.class,
                () -> Publicacion.publicar(vendedor, "Libro", BigDecimal.ZERO));
        assertThrows(ReglaDominioException.class,
                () -> Publicacion.publicar(vendedor, "  ", new BigDecimal("5000")));
    }

    @Test
    void soloElDuenoPuedeEliminarLogicamenteLaPublicacion() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> publicacion.eliminarLogicamente(comprador));
        assertEquals(EstadoPublicacion.ACTIVA, publicacion.getEstado());

        // Act
        publicacion.eliminarLogicamente(vendedor);

        // Assert
        assertEquals(EstadoPublicacion.ELIMINADA, publicacion.getEstado());
    }
}

