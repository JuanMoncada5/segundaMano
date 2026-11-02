package com.uniquindio.segundaMano.application.auth;

import com.uniquindio.segundaMano.support.CodificadorFalso;
import com.uniquindio.segundaMano.support.CredencialRepositoryEnMemoria;
import com.uniquindio.segundaMano.support.EmisorTokenFalso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutenticarUseCaseTest {

    private AutenticarUseCase useCase;

    @BeforeEach
    void preparar() {
        CredencialRepositoryEnMemoria credenciales = new CredencialRepositoryEnMemoria();
        CodificadorFalso codificador = new CodificadorFalso();
        credenciales.guardar(new Credencial("ana@uqvirtual.edu.co", codificador.codificar("Clave123*"), Rol.UNICAMBISTA));
        useCase = new AutenticarUseCase(credenciales, codificador, new EmisorTokenFalso());
    }

    @Test
    void deberiaEntregarUnTokenCuandoLasCredencialesSonCorrectas() {
        // Act
        LoginResponse respuesta = useCase.ejecutar(new LoginRequest("ana@uqvirtual.edu.co", "Clave123*"));

        // Assert
        assertEquals("Bearer", respuesta.tipo());
        assertEquals("ana@uqvirtual.edu.co", respuesta.correoInstitucional());
        assertEquals("UNICAMBISTA", respuesta.rol());
        assertTrue(respuesta.token().contains("ana@uqvirtual.edu.co"));
    }

    @Test
    void deberiaNormalizarElCorreoAntesDeBuscarlo() {
        // Act
        LoginResponse respuesta = useCase.ejecutar(new LoginRequest("  ANA@UQvirtual.edu.co ", "Clave123*"));

        // Assert
        assertEquals("ana@uqvirtual.edu.co", respuesta.correoInstitucional());
    }

    @Test
    void noDeberiaEntrarConUnaContrasenaIncorrecta() {
        assertThrows(CredencialesInvalidasException.class,
                () -> useCase.ejecutar(new LoginRequest("ana@uqvirtual.edu.co", "otra-clave")));
    }

    @Test
    void noDeberiaEntrarConUnCorreoQueNoExiste() {
        assertThrows(CredencialesInvalidasException.class,
                () -> useCase.ejecutar(new LoginRequest("nadie@uqvirtual.edu.co", "Clave123*")));
    }

    @Test
    void elMensajeDeErrorEsElMismoSeaElCorreoOLaContrasena() {
        // Arrange: si los mensajes fueran distintos, alguien podría averiguar qué correos están registrados
        var porCorreo = assertThrows(CredencialesInvalidasException.class,
                () -> useCase.ejecutar(new LoginRequest("nadie@uqvirtual.edu.co", "Clave123*")));
        var porContrasena = assertThrows(CredencialesInvalidasException.class,
                () -> useCase.ejecutar(new LoginRequest("ana@uqvirtual.edu.co", "mala")));

        // Assert
        assertEquals(porCorreo.getMessage(), porContrasena.getMessage());
    }
}
