package com.uniquindio.segundaMano.domain.entity;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniCambistaTest {

    @Test
    void dosUniCambistasConElMismoCorreoSonElMismo() {
        // Arrange
        UniCambista original = UniCambista.registrar("laura@uniquindio.edu.co", "Laura");
        UniCambista otro = UniCambista.registrar("laura@uniquindio.edu.co", "Nombre distinto");

        // Act & Assert
        assertEquals(original, otro); // Entidad: igual por IDENTIDAD (mismo correo), no por nombre
    }

    @Test
    void noDebePermitirRegistrarUniCambistaConCorreoNoInstitucional() {
        // Arrange & Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            UniCambista.registrar("laura@gmail.com", "Laura");
        });
    }

    @Test
    void aceptaCorreoDeEstudianteYLoNormaliza() {
        // Arrange & Act
        UniCambista estudiante = UniCambista.registrar("  Laura@UQvirtual.edu.co ", "Laura");
        UniCambista mismo = UniCambista.registrar("laura@uqvirtual.edu.co", "Laura");

        // Assert
        assertEquals("laura@uqvirtual.edu.co", estudiante.getCorreoInstitucional());
        assertEquals(estudiante, mismo);
        assertTrue(!estudiante.puedeCrearPublicacion()); // recién registrado aún no está verificado
    }
}