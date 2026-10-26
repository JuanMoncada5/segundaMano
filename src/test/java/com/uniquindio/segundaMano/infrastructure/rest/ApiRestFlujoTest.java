package com.uniquindio.segundaMano.infrastructure.rest;

import com.uniquindio.segundaMano.application.dto.OfertaResponse;
import com.uniquindio.segundaMano.application.dto.PublicacionResponse;
import com.uniquindio.segundaMano.application.dto.TransaccionResponse;
import com.uniquindio.segundaMano.application.usecase.AceptarOfertaUseCase;
import com.uniquindio.segundaMano.application.usecase.AsignarEncuentroSeguroUseCase;
import com.uniquindio.segundaMano.application.usecase.ConfirmarTransaccionUseCase;
import com.uniquindio.segundaMano.application.usecase.ConsultarPublicacionUseCase;
import com.uniquindio.segundaMano.application.usecase.ConsultarTransaccionUseCase;
import com.uniquindio.segundaMano.application.usecase.ConsultarUniCambistaUseCase;
import com.uniquindio.segundaMano.application.usecase.ListarPublicacionesActivasUseCase;
import com.uniquindio.segundaMano.application.usecase.ListarTransaccionesDeUniCambistaUseCase;
import com.uniquindio.segundaMano.application.usecase.OfertarArticuloUseCase;
import com.uniquindio.segundaMano.application.usecase.PublicarArticuloUseCase;
import com.uniquindio.segundaMano.application.usecase.RegistrarUniCambistaUseCase;
import com.uniquindio.segundaMano.application.usecase.VerificarCorreoUseCase;
import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import com.uniquindio.segundaMano.infrastructure.persistence.PublicacionRepositoryEnMemoria;
import com.uniquindio.segundaMano.infrastructure.persistence.TransaccionRepositoryEnMemoria;
import com.uniquindio.segundaMano.infrastructure.persistence.UniCambistaRepositoryEnMemoria;
import com.uniquindio.segundaMano.infrastructure.rest.dto.AceptarOfertaBody;
import com.uniquindio.segundaMano.infrastructure.rest.dto.ConfirmacionBody;
import com.uniquindio.segundaMano.infrastructure.rest.dto.EncuentroSeguroBody;
import com.uniquindio.segundaMano.infrastructure.rest.dto.OfertarArticuloBody;
import com.uniquindio.segundaMano.infrastructure.rest.dto.PublicarArticuloBody;
import com.uniquindio.segundaMano.infrastructure.rest.dto.RegistrarUniCambistaBody;
import com.uniquindio.segundaMano.infrastructure.rest.dto.VerificarCorreoBody;
import com.uniquindio.segundaMano.infrastructure.rest.exception.ErrorApi;
import com.uniquindio.segundaMano.infrastructure.rest.exception.ManejadorGlobalDeExcepciones;
import com.uniquindio.segundaMano.infrastructure.rest.mapper.PublicacionRestMapper;
import com.uniquindio.segundaMano.infrastructure.rest.mapper.TransaccionRestMapper;
import com.uniquindio.segundaMano.infrastructure.rest.mapper.UniCambistaRestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba la capa rest SIN levantar Spring: los controllers son clases Java normales,
 * así que se crean con new y se llaman como métodos (igual que lo haría Spring con una petición HTTP).
 */
class ApiRestFlujoTest {

    private static final String VENDEDOR = "vendedor@uniquindio.edu.co";
    private static final String COMPRADOR = "comprador@uqvirtual.edu.co";

    private UniCambistaController uniCambistaController;
    private PublicacionController publicacionController;
    private ConsultaPublicacionesController consultaPublicaciones;
    private TransaccionController transaccionController;
    private ConsultaTransaccionesController consultaTransacciones;

    @BeforeEach
    void preparar() {
        PublicacionRepository publicaciones = new PublicacionRepositoryEnMemoria();
        TransaccionRepository transacciones = new TransaccionRepositoryEnMemoria();
        UniCambistaRepository uniCambistas = new UniCambistaRepositoryEnMemoria();

        uniCambistaController = new UniCambistaController(
                new RegistrarUniCambistaUseCase(uniCambistas), new VerificarCorreoUseCase(uniCambistas),
                new UniCambistaRestMapper());
        publicacionController = new PublicacionController(
                new PublicarArticuloUseCase(publicaciones, uniCambistas),
                new OfertarArticuloUseCase(publicaciones, uniCambistas),
                new AceptarOfertaUseCase(publicaciones, transacciones, uniCambistas),
                new PublicacionRestMapper());
        consultaPublicaciones = new ConsultaPublicacionesController(new ListarPublicacionesActivasUseCase(publicaciones),
                new ConsultarPublicacionUseCase(publicaciones));
        transaccionController = new TransaccionController(
                new AsignarEncuentroSeguroUseCase(transacciones, uniCambistas),
                new ConfirmarTransaccionUseCase(transacciones, uniCambistas),
                new TransaccionRestMapper());
        consultaTransacciones = new ConsultaTransaccionesController(
                new ConsultarTransaccionUseCase(transacciones),
                new ListarTransaccionesDeUniCambistaUseCase(transacciones, uniCambistas));
    }

    private void registrarYVerificar(String correo, String nombre) {
        uniCambistaController.registrar(new RegistrarUniCambistaBody(correo, nombre));
        uniCambistaController.verificarCorreo(new VerificarCorreoBody(correo));
    }

    @Test
    void flujoCompletoPorLosControllersTerminaConLaTransaccionCompletada() {
        // Arrange
        registrarYVerificar(VENDEDOR, "Laura");
        registrarYVerificar(COMPRADOR, "Juan");

        // Act
        PublicacionResponse publicacion = publicacionController.publicar(
                new PublicarArticuloBody(VENDEDOR, "Calculadora Casio", new BigDecimal("150000"), null, null)).getBody();
        OfertaResponse oferta = publicacionController.ofertar(publicacion.codigo(),
                new OfertarArticuloBody(COMPRADOR, new BigDecimal("100000"))).getBody();
        TransaccionResponse iniciada = publicacionController.aceptarOferta(
                publicacion.codigo(), oferta.id(), new AceptarOfertaBody(VENDEDOR)).getBody();
        transaccionController.asignarEncuentroSeguro(iniciada.id(),
                new EncuentroSeguroBody(COMPRADOR, "biblioteca", "Central"));
        transaccionController.confirmar(iniciada.id(), new ConfirmacionBody(COMPRADOR));
        TransaccionResponse completada = transaccionController.confirmar(iniciada.id(), new ConfirmacionBody(VENDEDOR));

        // Assert
        assertEquals("COMPLETADA", completada.estado());
        assertEquals("BIBLIOTECA", completada.zonaEncuentro());
        assertTrue(consultaPublicaciones.listarActivas().isEmpty());
        assertEquals("COMPLETADA", consultaTransacciones.consultar(iniciada.id()).estado());
        assertEquals(1, consultaTransacciones.listarDeUniCambista(COMPRADOR).size());
    }

    @Test
    void publicarConVigenciaAcademicaYListarActivas() {
        // Arrange
        registrarYVerificar(VENDEDOR, "Laura");

        // Act
        ResponseEntity<PublicacionResponse> respuesta = publicacionController.publicar(
                new PublicarArticuloBody(VENDEDOR, "Libro de Cálculo", new BigDecimal("50000"), "Cálculo I", "2026-2"));
        PublicacionResponse publicacion = respuesta.getBody();

        // Assert
        assertEquals(201, respuesta.getStatusCode().value());
        assertEquals("/api/publicaciones/" + publicacion.codigo(), respuesta.getHeaders().getLocation().toString());
        assertEquals("Cálculo I - 2026-2", publicacion.etiquetaVigenciaAcademica());
        assertEquals(1, consultaPublicaciones.listarActivas().size());
    }

    @Test
    void elManejadorConvierteLaReglaDeDominioEnUn400() {
        ErrorApi error = new ManejadorGlobalDeExcepciones()
                .manejarReglaDominio(new ReglaDominioException("El correo debe ser institucional"));

        assertEquals(400, error.estado());
        assertEquals("El correo debe ser institucional", error.mensaje());
    }

    @Test
    void elManejadorConvierteLoQueNoExisteEnUn404() {
        ErrorApi error = new ManejadorGlobalDeExcepciones()
                .manejarNoEncontrado(new RecursoNoEncontradoException("La Transacción no existe"));

        assertEquals(404, error.estado());
    }

    @Test
    void elManejadorOcultaLosDetallesDeUnErrorInesperadoEnUn500() {
        ErrorApi error = new ManejadorGlobalDeExcepciones()
                .manejarInesperado(new IllegalStateException("detalle interno secreto"));

        assertEquals(500, error.estado());
        assertFalse(error.mensaje().contains("secreto"));
    }

    @Test
    void consultarAlgoQueNoExisteLanzaRecursoNoEncontrado() {
        assertThrows(RecursoNoEncontradoException.class, () -> consultaTransacciones.consultar("no-existe"));
        assertThrows(RecursoNoEncontradoException.class, () -> consultaPublicaciones.consultar("no-existe"));
    }

    @Test
    void unaReglaRotaSaleComoReglaDominioExceptionParaQueElManejadorLaAtienda() {
        assertThrows(ReglaDominioException.class,
                () -> uniCambistaController.registrar(new RegistrarUniCambistaBody("laura@gmail.com", "Laura")));
    }
}
