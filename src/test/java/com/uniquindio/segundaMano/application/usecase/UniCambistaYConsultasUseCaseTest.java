package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.AceptarOfertaRequest;
import com.uniquindio.segundaMano.application.dto.OfertarArticuloRequest;
import com.uniquindio.segundaMano.application.dto.PublicacionResponse;
import com.uniquindio.segundaMano.application.dto.PublicarArticuloRequest;
import com.uniquindio.segundaMano.application.dto.RegistrarUniCambistaRequest;
import com.uniquindio.segundaMano.application.dto.TransaccionResponse;
import com.uniquindio.segundaMano.application.dto.UniCambistaResponse;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import com.uniquindio.segundaMano.infrastructure.persistence.PublicacionRepositoryEnMemoria;
import com.uniquindio.segundaMano.infrastructure.persistence.TransaccionRepositoryEnMemoria;
import com.uniquindio.segundaMano.infrastructure.persistence.UniCambistaRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UniCambistaYConsultasUseCaseTest {

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
    }

    @Test
    void registrarUniCambistaLoGuardaSinVerificar() {
        // Act
        UniCambistaResponse respuesta = new RegistrarUniCambistaUseCase(uniCambistas)
                .ejecutar(new RegistrarUniCambistaRequest("  Laura@UQvirtual.edu.co ", "Laura"));

        // Assert
        assertEquals("laura@uqvirtual.edu.co", respuesta.correoInstitucional());
        assertFalse(respuesta.verificado());
        assertTrue(uniCambistas.buscarPorCorreo("laura@uqvirtual.edu.co").isPresent());
    }

    @Test
    void registrarDosVecesElMismoCorreoLanzaExcepcionDeDominio() {
        // Arrange
        RegistrarUniCambistaUseCase registrar = new RegistrarUniCambistaUseCase(uniCambistas);
        registrar.ejecutar(new RegistrarUniCambistaRequest(CORREO_VENDEDOR, "Laura"));

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> registrar.ejecutar(new RegistrarUniCambistaRequest(CORREO_VENDEDOR, "Otra Laura")));
    }

    @Test
    void registrarConCorreoNoInstitucionalLanzaExcepcionDeDominio() {
        assertThrows(ReglaDominioException.class, () -> new RegistrarUniCambistaUseCase(uniCambistas)
                .ejecutar(new RegistrarUniCambistaRequest("laura@gmail.com", "Laura")));
    }

    @Test
    void verificarCorreoHabilitaAlUniCambistaParaPublicar() {
        // Arrange
        new RegistrarUniCambistaUseCase(uniCambistas)
                .ejecutar(new RegistrarUniCambistaRequest(CORREO_VENDEDOR, "Laura"));
        PublicarArticuloUseCase publicar = new PublicarArticuloUseCase(publicaciones, uniCambistas);
        PublicarArticuloRequest request = new PublicarArticuloRequest(CORREO_VENDEDOR, "Calculadora", new BigDecimal("1000"));
        assertThrows(ReglaDominioException.class, () -> publicar.ejecutar(request));

        // Act
        UniCambistaResponse verificado = new VerificarCorreoUseCase(uniCambistas).ejecutar(CORREO_VENDEDOR);

        // Assert
        assertTrue(verificado.verificado());
        assertDoesNotThrow(() -> publicar.ejecutar(request));
    }

    @Test
    void verificarCorreoDeUniCambistaNoRegistradoLanzaExcepcionDeDominio() {
        assertThrows(ReglaDominioException.class,
                () -> new VerificarCorreoUseCase(uniCambistas).ejecutar("fantasma@uqvirtual.edu.co"));
    }

    @Test
    void listarPublicacionesActivasSoloDevuelveLasActivas() {
        // Arrange
        registrarYVerificar(CORREO_VENDEDOR, "Laura");
        registrarYVerificar(CORREO_COMPRADOR, "Juan");
        PublicarArticuloUseCase publicar = new PublicarArticuloUseCase(publicaciones, uniCambistas);
        PublicacionResponse activa = publicar.ejecutar(new PublicarArticuloRequest(CORREO_VENDEDOR, "Libro", new BigDecimal("500")));
        PublicacionResponse vendida = publicar.ejecutar(new PublicarArticuloRequest(CORREO_VENDEDOR, "Mesa", new BigDecimal("900")));
        var oferta = new OfertarArticuloUseCase(publicaciones, uniCambistas)
                .ejecutar(new OfertarArticuloRequest(vendida.codigo(), CORREO_COMPRADOR, new BigDecimal("900")));
        new AceptarOfertaUseCase(publicaciones, transacciones, uniCambistas)
                .ejecutar(new AceptarOfertaRequest(vendida.codigo(), oferta.id(), CORREO_VENDEDOR));

        // Act
        List<PublicacionResponse> resultado = new ListarPublicacionesActivasUseCase(publicaciones).ejecutar();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(activa.codigo(), resultado.get(0).codigo());
    }

    @Test
    void consultarYListarTransaccionesDeUnUniCambista() {
        // Arrange
        registrarYVerificar(CORREO_VENDEDOR, "Laura");
        registrarYVerificar(CORREO_COMPRADOR, "Juan");
        PublicacionResponse publicacion = new PublicarArticuloUseCase(publicaciones, uniCambistas)
                .ejecutar(new PublicarArticuloRequest(CORREO_VENDEDOR, "Libro", new BigDecimal("500")));
        var oferta = new OfertarArticuloUseCase(publicaciones, uniCambistas)
                .ejecutar(new OfertarArticuloRequest(publicacion.codigo(), CORREO_COMPRADOR, new BigDecimal("400")));
        TransaccionResponse creada = new AceptarOfertaUseCase(publicaciones, transacciones, uniCambistas)
                .ejecutar(new AceptarOfertaRequest(publicacion.codigo(), oferta.id(), CORREO_VENDEDOR));

        // Act
        TransaccionResponse consultada = new ConsultarTransaccionUseCase(transacciones).ejecutar(creada.id());
        List<TransaccionResponse> delComprador = new ListarTransaccionesDeUniCambistaUseCase(transacciones, uniCambistas)
                .ejecutar(CORREO_COMPRADOR);

        // Assert
        assertEquals(creada.id(), consultada.id());
        assertEquals("PENDIENTE", consultada.estado());
        assertEquals(1, delComprador.size());
        assertThrows(ReglaDominioException.class, () -> new ConsultarTransaccionUseCase(transacciones).ejecutar("no-existe"));
        assertThrows(ReglaDominioException.class,
                () -> new ListarTransaccionesDeUniCambistaUseCase(transacciones, uniCambistas).ejecutar("fantasma@uqvirtual.edu.co"));
    }

    private void registrarYVerificar(String correo, String nombre) {
        new RegistrarUniCambistaUseCase(uniCambistas).ejecutar(new RegistrarUniCambistaRequest(correo, nombre));
        new VerificarCorreoUseCase(uniCambistas).ejecutar(correo);
    }
}
