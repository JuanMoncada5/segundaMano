package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.AceptarOfertaRequest;
import com.uniquindio.segundaMano.application.dto.AsignarEncuentroSeguroRequest;
import com.uniquindio.segundaMano.application.dto.ConfirmarTransaccionRequest;
import com.uniquindio.segundaMano.application.dto.OfertaResponse;
import com.uniquindio.segundaMano.application.dto.OfertarArticuloRequest;
import com.uniquindio.segundaMano.application.dto.PublicacionResponse;
import com.uniquindio.segundaMano.application.dto.PublicarArticuloRequest;
import com.uniquindio.segundaMano.application.dto.TransaccionResponse;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;
import com.uniquindio.segundaMano.infrastructure.persistence.PublicacionRepositoryEnMemoria;
import com.uniquindio.segundaMano.infrastructure.persistence.TransaccionRepositoryEnMemoria;
import com.uniquindio.segundaMano.infrastructure.persistence.UniCambistaRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CasosDeUsoTest {

    private static final String CORREO_VENDEDOR = "vendedor@uniquindio.edu.co";
    private static final String CORREO_COMPRADOR = "comprador@uqvirtual.edu.co";

    private PublicacionRepository publicaciones;
    private TransaccionRepository transacciones;
    private UniCambistaRepository uniCambistas;

    @BeforeEach
    void preparar() {
        publicaciones = new PublicacionRepositoryEnMemoria();
        transacciones = new TransaccionRepositoryEnMemoria();
        uniCambistas = new UniCambistaRepositoryEnMemoria();

        UniCambista vendedor = UniCambista.registrar(CORREO_VENDEDOR, "Laura");
        vendedor.verificarCorreo();
        UniCambista comprador = UniCambista.registrar(CORREO_COMPRADOR, "Juan");
        comprador.verificarCorreo();
        uniCambistas.guardar(vendedor);
        uniCambistas.guardar(comprador);
    }

    private PublicacionResponse publicarCalculadora() {
        return new PublicarArticuloUseCase(publicaciones, uniCambistas)
                .ejecutar(new PublicarArticuloRequest(CORREO_VENDEDOR, "Calculadora Casio", new BigDecimal("150000")));
    }

    @Test
    void publicarArticuloGuardaLaPublicacionActivaEnElRepositorio() {
        // Act
        PublicacionResponse respuesta = publicarCalculadora();

        // Assert
        assertTrue(publicaciones.buscarPorCodigo(respuesta.codigo()).isPresent());
        assertEquals("ACTIVA", respuesta.estado());
        assertEquals(CORREO_VENDEDOR, respuesta.correoDueno());
        assertEquals(1, publicaciones.listarActivas().size());
    }

    @Test
    void publicarArticuloConVigenciaAcademicaLaMuestraEnLaRespuesta() {
        // Arrange
        PublicarArticuloUseCase publicar = new PublicarArticuloUseCase(publicaciones, uniCambistas);
        PublicarArticuloRequest request = new PublicarArticuloRequest(
                CORREO_VENDEDOR, "Libro de Cálculo", new BigDecimal("50000"), "Cálculo I", "2026-2");

        // Act
        PublicacionResponse respuesta = publicar.ejecutar(request);

        // Assert
        assertEquals("Cálculo I - 2026-2", respuesta.etiquetaVigenciaAcademica());
    }

    @Test
    void publicarArticuloConUniCambistaNoVerificadoLanzaExcepcionDeDominio() {
        // Arrange
        uniCambistas.guardar(UniCambista.registrar("nuevo@uqvirtual.edu.co", "Pedro"));
        PublicarArticuloUseCase publicar = new PublicarArticuloUseCase(publicaciones, uniCambistas);
        PublicarArticuloRequest request =
                new PublicarArticuloRequest("nuevo@uqvirtual.edu.co", "Libro de Cálculo", new BigDecimal("50000"));

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> publicar.ejecutar(request));
        assertTrue(publicaciones.listarActivas().isEmpty());
    }

    @Test
    void publicarArticuloConCorreoNoRegistradoLanzaExcepcionDeDominio() {
        // Arrange
        PublicarArticuloUseCase publicar = new PublicarArticuloUseCase(publicaciones, uniCambistas);
        PublicarArticuloRequest request = new PublicarArticuloRequest("fantasma@uqvirtual.edu.co", "Libro", new BigDecimal("50000"));

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> publicar.ejecutar(request));
    }

    @Test
    void ofertarArticuloGuardaLaOfertaDentroDeLaPublicacion() {
        // Arrange
        PublicacionResponse publicacion = publicarCalculadora();
        OfertarArticuloUseCase ofertar = new OfertarArticuloUseCase(publicaciones, uniCambistas);

        // Act
        OfertaResponse oferta = ofertar.ejecutar(
                new OfertarArticuloRequest(publicacion.codigo(), CORREO_COMPRADOR, new BigDecimal("100000")));

        // Assert
        assertEquals("PENDIENTE", oferta.estado());
        assertEquals(CORREO_COMPRADOR, oferta.correoOferente());
        assertEquals(1, publicaciones.buscarPorCodigo(publicacion.codigo()).orElseThrow().getOfertas().size());
    }

    @Test
    void asignarEncuentroSeguroConZonaQueNoEsDelCampusLanzaExcepcionDeDominio() {
        // Arrange
        PublicacionResponse publicacion = publicarCalculadora();
        OfertaResponse oferta = new OfertarArticuloUseCase(publicaciones, uniCambistas).ejecutar(
                new OfertarArticuloRequest(publicacion.codigo(), CORREO_COMPRADOR, new BigDecimal("100000")));
        TransaccionResponse transaccion = new AceptarOfertaUseCase(publicaciones, transacciones, uniCambistas)
                .ejecutar(new AceptarOfertaRequest(publicacion.codigo(), oferta.id(), CORREO_VENDEDOR));
        AsignarEncuentroSeguroUseCase asignar = new AsignarEncuentroSeguroUseCase(transacciones, uniCambistas);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> asignar.ejecutar(
                new AsignarEncuentroSeguroRequest(transaccion.id(), CORREO_COMPRADOR, "Centro Comercial", null)));
    }

    @Test
    void flujoCompletoDeCasosDeUsoTerminaConLaTransaccionCompletada() {
        // Arrange
        PublicacionResponse publicacion = publicarCalculadora();
        OfertaResponse oferta = new OfertarArticuloUseCase(publicaciones, uniCambistas).ejecutar(
                new OfertarArticuloRequest(publicacion.codigo(), CORREO_COMPRADOR, new BigDecimal("100000")));
        AceptarOfertaUseCase aceptar = new AceptarOfertaUseCase(publicaciones, transacciones, uniCambistas);
        AsignarEncuentroSeguroUseCase asignar = new AsignarEncuentroSeguroUseCase(transacciones, uniCambistas);
        ConfirmarTransaccionUseCase confirmar = new ConfirmarTransaccionUseCase(transacciones, uniCambistas);

        // Act
        TransaccionResponse iniciada = aceptar.ejecutar(
                new AceptarOfertaRequest(publicacion.codigo(), oferta.id(), CORREO_VENDEDOR));
        asignar.ejecutar(new AsignarEncuentroSeguroRequest(iniciada.id(), CORREO_COMPRADOR, "biblioteca", "Central"));
        confirmar.ejecutar(new ConfirmarTransaccionRequest(iniciada.id(), CORREO_COMPRADOR));
        TransaccionResponse completada = confirmar.ejecutar(new ConfirmarTransaccionRequest(iniciada.id(), CORREO_VENDEDOR));

        // Assert
        assertEquals(new BigDecimal("100000"), iniciada.montoAcordado());
        assertEquals("COMPLETADA", completada.estado());
        assertEquals("BIBLIOTECA", completada.zonaEncuentro());
        assertEquals("Central", completada.referenciaEncuentro());
        assertEquals(EstadoPublicacion.EN_PROCESO,
                publicaciones.buscarPorCodigo(publicacion.codigo()).orElseThrow().getEstado());
        UniCambista comprador = uniCambistas.buscarPorCorreo(CORREO_COMPRADOR).orElseThrow();
        assertEquals(1, transacciones.listarPorParticipante(comprador).size());
    }

    @Test
    void expirarPublicacionesSoloExpiraLasQueLlevan60DiasSinActividad() {
        // Arrange
        publicarCalculadora();
        ExpirarPublicacionesUseCase expirar = new ExpirarPublicacionesUseCase(publicaciones);

        // Act
        int antes = expirar.ejecutar(LocalDate.now().plusDays(59));
        int despues = expirar.ejecutar(LocalDate.now().plusDays(60));

        // Assert
        assertEquals(0, antes);
        assertEquals(1, despues);
        assertTrue(publicaciones.listarActivas().isEmpty());
    }
}