package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Repositorios simulados con Mockito: así se puede comprobar si guardar se llamó o no. */
class EliminarPublicacionUseCaseTest {

    private PublicacionRepository publicaciones;
    private UniCambistaRepository uniCambistas;
    private EliminarPublicacionUseCase eliminar;
    private UniCambista ana;
    private UniCambista luis;
    private Publicacion silla;

    @BeforeEach
    void preparar() {
        publicaciones = mock(PublicacionRepository.class);
        uniCambistas = mock(UniCambistaRepository.class);
        eliminar = new EliminarPublicacionUseCase(publicaciones, uniCambistas);

        ana = UniCambista.registrar("ana@uqvirtual.edu.co", "Ana");
        ana.verificarCorreo();
        luis = UniCambista.registrar("luis@uqvirtual.edu.co", "Luis");
        luis.verificarCorreo();
        silla = Publicacion.publicar(ana, "Silla de escritorio", new BigDecimal("60000"));

        when(publicaciones.buscarPorCodigo("PUB-SILLA")).thenReturn(Optional.of(silla));
        when(uniCambistas.buscarPorCorreo("ana@uqvirtual.edu.co")).thenReturn(Optional.of(ana));
        when(uniCambistas.buscarPorCorreo("luis@uqvirtual.edu.co")).thenReturn(Optional.of(luis));
    }

    @Test
    void elDuenoEliminaYLaPublicacionQuedaEliminadaSinBorrarse() {
        // Act
        eliminar.ejecutar("PUB-SILLA", "ana@uqvirtual.edu.co");

        // Assert: no se borra, cambia de estado y se guarda
        assertEquals(EstadoPublicacion.ELIMINADA, silla.getEstado());
        verify(publicaciones).guardar(silla);
    }

    @Test
    void quienNoEsElDuenoNoPuedeEliminarYNoSeGuardaNada() {
        // Act & Assert: la regla la lanza el dominio (Publicacion.eliminarLogicamente)
        assertThrows(ReglaDominioException.class, () -> eliminar.ejecutar("PUB-SILLA", "luis@uqvirtual.edu.co"));
        assertEquals(EstadoPublicacion.ACTIVA, silla.getEstado());
        verify(publicaciones, never()).guardar(any());
    }

    @Test
    void eliminarUnaPublicacionQueNoExisteNoGuardaNada() {
        // Arrange
        when(publicaciones.buscarPorCodigo("NO-EXISTE")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> eliminar.ejecutar("NO-EXISTE", "ana@uqvirtual.edu.co"));
        verify(publicaciones, never()).guardar(any());
    }
}