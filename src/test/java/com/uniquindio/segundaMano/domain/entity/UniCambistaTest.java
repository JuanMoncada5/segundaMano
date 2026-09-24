package com.uniquindio.segundaMano.domain.entity;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}