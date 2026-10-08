package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.domain.entity.Oferta;
import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.Transaccion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;
import com.uniquindio.segundaMano.domain.valueObject.EstadoOferta;
import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;
import com.uniquindio.segundaMano.domain.valueObject.EstadoTransaccion;
import com.uniquindio.segundaMano.infrastructure.persistence.PublicacionRepositoryEnMemoria;
import com.uniquindio.segundaMano.infrastructure.persistence.TransaccionRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CasosDeUsoTest {

    private PublicacionRepository publicaciones;
    private TransaccionRepository transacciones;
    private UniCambista vendedor;
    private UniCambista comprador;

    @BeforeEach
    void preparar() {
        publicaciones = new PublicacionRepositoryEnMemoria();
        transacciones = new TransaccionRepositoryEnMemoria();
        vendedor = UniCambista.registrar("vendedor@uniquindio.edu.co", "Laura");
        vendedor.verificarCorreo();
        comprador = UniCambista.registrar("comprador@uniquindio.edu.co", "Juan");
        comprador.verificarCorreo();
    }

    @Test
    void publicarArticuloGuardaLaPublicacionActivaEnElRepositorio() {
        // Arrange
        PublicarArticuloUseCase publicar = new PublicarArticuloUseCase(publicaciones);

        // Act
        Publicacion publicacion = publicar.ejecutar(vendedor, "Calculadora Casio", new BigDecimal("150000"));

        // Assert
        assertTrue(publicaciones.buscarPorCodigo(publicacion.getCodigo()).isPresent());
        assertEquals(1, publicaciones.listarActivas().size());
    }

    @Test
    void publicarArticuloConUniCambistaNoVerificadoLanzaExcepcionDeDominio() {
        // Arrange
        UniCambista sinVerificar = UniCambista.registrar("nuevo@uniquindio.edu.co", "Pedro");
        PublicarArticuloUseCase publicar = new PublicarArticuloUseCase(publicaciones);

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> publicar.ejecutar(sinVerificar, "Libro de Cálculo", new BigDecimal("50000")));
        assertTrue(publicaciones.listarActivas().isEmpty());
    }

    @Test
    void ofertarArticuloGuardaLaOfertaDentroDeLaPublicacion() {
        // Arrange
        Publicacion publicacion = new PublicarArticuloUseCase(publicaciones)
                .ejecutar(vendedor, "Calculadora Casio", new BigDecimal("150000"));
        OfertarArticuloUseCase ofertar = new OfertarArticuloUseCase(publicaciones);

        // Act
        Oferta oferta = ofertar.ejecutar(publicacion.getCodigo(), comprador, new BigDecimal("100000"));

        // Assert
        assertEquals(EstadoOferta.PENDIENTE, oferta.getEstado());
        assertEquals(1, publicaciones.buscarPorCodigo(publicacion.getCodigo()).orElseThrow().getOfertas().size());
    }

    @Test
    void flujoCompletoDeCasosDeUsoTerminaConLaTransaccionCompletada() {
        // Arrange
        Publicacion publicacion = new PublicarArticuloUseCase(publicaciones)
                .ejecutar(vendedor, "Calculadora Casio", new BigDecimal("150000"));
        Oferta oferta = new OfertarArticuloUseCase(publicaciones)
                .ejecutar(publicacion.getCodigo(), comprador, new BigDecimal("100000"));
        AceptarOfertaUseCase aceptar = new AceptarOfertaUseCase(publicaciones, transacciones);
        ConfirmarTransaccionUseCase confirmar = new ConfirmarTransaccionUseCase(transacciones);

        // Act
        Transaccion transaccion = aceptar.ejecutar(publicacion.getCodigo(), oferta.getId(), vendedor);
        confirmar.ejecutar(transaccion.getId(), comprador);
        confirmar.ejecutar(transaccion.getId(), vendedor);

        // Assert
        assertEquals(EstadoPublicacion.EN_PROCESO, publicacion.getEstado());
        assertEquals(EstadoTransaccion.COMPLETADA,
                transacciones.buscarPorId(transaccion.getId()).orElseThrow().getEstado());
        assertEquals(1, transacciones.listarPorParticipante(comprador).size());
    }
}