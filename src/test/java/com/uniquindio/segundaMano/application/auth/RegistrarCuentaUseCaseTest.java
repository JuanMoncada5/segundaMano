package com.uniquindio.segundaMano.application.auth;

import com.uniquindio.segundaMano.application.dto.UniCambistaResponse;
import com.uniquindio.segundaMano.application.usecase.RegistrarUniCambistaUseCase;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import com.uniquindio.segundaMano.infrastructure.persistence.UniCambistaRepositoryEnMemoria;
import com.uniquindio.segundaMano.support.CodificadorFalso;
import com.uniquindio.segundaMano.support.CredencialRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistrarCuentaUseCaseTest {

    private CredencialRepositoryEnMemoria credenciales;
    private UniCambistaRepository uniCambistas;
    private RegistrarCuentaUseCase useCase;

    @BeforeEach
    void preparar() {
        credenciales = new CredencialRepositoryEnMemoria();
        uniCambistas = new UniCambistaRepositoryEnMemoria();
        useCase = new RegistrarCuentaUseCase(
                new RegistrarUniCambistaUseCase(uniCambistas), credenciales, new CodificadorFalso());
    }

    @Test
    void deberiaGuardarLaCredencialConLaContrasenaCodificadaYElRolUniCambista() {
        // Act
        UniCambistaResponse respuesta = useCase.ejecutar(
                new RegistrarCuentaRequest("Laura@uqvirtual.edu.co", "Laura", "Clave123*"));

        // Assert
        Credencial guardada = credenciales.buscarPorCorreo("laura@uqvirtual.edu.co").orElseThrow();
        assertEquals("laura@uqvirtual.edu.co", respuesta.correoInstitucional());
        assertEquals(Rol.UNICAMBISTA, guardada.rol());
        assertNotEquals("Clave123*", guardada.contrasenaHash()); // nunca en texto plano
    }

    @Test
    void noDeberiaGuardarCredencialSiElCorreoNoEsInstitucional() {
        assertThrows(ReglaDominioException.class, () -> useCase.ejecutar(
                new RegistrarCuentaRequest("laura@gmail.com", "Laura", "Clave123*")));
        assertEquals(0, credenciales.cantidad());
    }

    @Test
    void noDeberiaGuardarNadaSiLaContrasenaEsMuyCorta() {
        assertThrows(ReglaDominioException.class, () -> useCase.ejecutar(
                new RegistrarCuentaRequest("laura@uqvirtual.edu.co", "Laura", "corta")));
        assertEquals(0, credenciales.cantidad());
        assertEquals(true, uniCambistas.buscarPorCorreo("laura@uqvirtual.edu.co").isEmpty());
    }

    @Test
    void noDeberiaPermitirRegistrarDosVecesElMismoCorreoNiCambiarLaContrasena() {
        // Arrange
        useCase.ejecutar(new RegistrarCuentaRequest("laura@uqvirtual.edu.co", "Laura", "Clave123*"));
        String hashOriginal = credenciales.buscarPorCorreo("laura@uqvirtual.edu.co").orElseThrow().contrasenaHash();

        // Act + Assert
        assertThrows(ReglaDominioException.class, () -> useCase.ejecutar(
                new RegistrarCuentaRequest("laura@uqvirtual.edu.co", "Intrusa", "OtraClave999")));
        assertEquals(hashOriginal, credenciales.buscarPorCorreo("laura@uqvirtual.edu.co").orElseThrow().contrasenaHash());
    }
}
