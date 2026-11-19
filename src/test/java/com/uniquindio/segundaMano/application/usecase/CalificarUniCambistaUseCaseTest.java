package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.CalificarUniCambistaRequest;
import com.uniquindio.segundaMano.application.dto.UniCambistaResponse;
import com.uniquindio.segundaMano.domain.entity.Oferta;
import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.Transaccion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import com.uniquindio.segundaMano.domain.valueObject.EncuentroSeguro;
import com.uniquindio.segundaMano.domain.valueObject.ZonaCampus;
import com.uniquindio.segundaMano.infrastructure.persistence.TransaccionRepositoryEnMemoria;
import com.uniquindio.segundaMano.infrastructure.persistence.UniCambistaRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CalificarUniCambistaUseCaseTest {

    private TransaccionRepository transacciones;
    private UniCambistaRepository uniCambistas;
    private CalificarUniCambistaUseCase calificar;
    private UniCambista ana;   // vendedora
    private UniCambista luis;  // comprador

    @BeforeEach
    void preparar() {
        transacciones = new TransaccionRepositoryEnMemoria();
        uniCambistas = new UniCambistaRepositoryEnMemoria();
        calificar = new CalificarUniCambistaUseCase(transacciones, uniCambistas);
        ana = verificado("ana@uqvirtual.edu.co", "Ana");
        luis = verificado("luis@uqvirtual.edu.co", "Luis");
    }

    private UniCambista verificado(String correo, String nombre) {
        UniCambista u = UniCambista.registrar(correo, nombre);
        u.verificarCorreo();
        uniCambistas.guardar(u);
        return u;
    }

    /** Arma una Transacción entre ana (vendedora) y luis (comprador); si completar es true, la confirman los dos. */
    private Transaccion transaccion(boolean completar) {
        Publicacion silla = Publicacion.publicar(ana, "Silla de escritorio", new BigDecimal("60000"));
        Oferta oferta = silla.recibirOferta(luis, new BigDecimal("50000"));
        silla.aceptarOferta(oferta.getId(), ana);
        Transaccion transaccion = Transaccion.iniciar(oferta);
        if (completar) {
            transaccion.asignarEncuentroSeguro(luis, new EncuentroSeguro(ZonaCampus.BIBLIOTECA));
            transaccion.confirmar(luis);
            transaccion.confirmar(ana);
        }
        transacciones.guardar(transaccion);
        return transaccion;
    }

    @Test
    void elCompradorCalificaALaVendedoraYSuRachaSeActualiza() {
        // Arrange
        Transaccion completada = transaccion(true);

        // Act: luis le pone 4 estrellas a ana
        UniCambistaResponse calificada = calificar.ejecutar(
                new CalificarUniCambistaRequest(completada.getId(), "luis@uqvirtual.edu.co", 4));

        // Assert: la respuesta es de ana, con 1 transacción calificada y promedio 4.0
        assertEquals("ana@uqvirtual.edu.co", calificada.correoInstitucional());
        assertEquals(4.0, calificada.promedioEstrellas(), 0.001);
        assertEquals(1, calificada.totalTransacciones());
        assertTrue(completada.isCalificadaPorComprador());
    }

    @Test
    void nadiePuedeCalificarDosVecesLaMismaTransaccion() {
        // Arrange
        Transaccion completada = transaccion(true);
        calificar.ejecutar(new CalificarUniCambistaRequest(completada.getId(), "luis@uqvirtual.edu.co", 5));

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> calificar.ejecutar(
                new CalificarUniCambistaRequest(completada.getId(), "luis@uqvirtual.edu.co", 1)));
        assertEquals(1, ana.getRachaConfianza().totalTransacciones());
    }

    @Test
    void quienNoParticipoNoPuedeCalificar() {
        // Arrange
        Transaccion completada = transaccion(true);
        verificado("sofia@uqvirtual.edu.co", "Sofía");

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> calificar.ejecutar(
                new CalificarUniCambistaRequest(completada.getId(), "sofia@uqvirtual.edu.co", 5)));
    }

    @Test
    void noSePuedeCalificarUnaTransaccionQueNoEstaCompletada() {
        // Arrange
        Transaccion pendiente = transaccion(false);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> calificar.ejecutar(
                new CalificarUniCambistaRequest(pendiente.getId(), "luis@uqvirtual.edu.co", 5)));
        assertEquals(0, ana.getRachaConfianza().totalTransacciones());
    }

    @Test
    void calificarUnaTransaccionQueNoExisteEsUn404() {
        assertThrows(RecursoNoEncontradoException.class, () -> calificar.ejecutar(
                new CalificarUniCambistaRequest("NO-EXISTE", "luis@uqvirtual.edu.co", 5)));
    }
}