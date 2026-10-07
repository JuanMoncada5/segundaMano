package com.uniquindio.segundaMano.domain.valueObject;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncuentroSeguroTest {

    @Test
    void dosEncuentrosSegurosConLaMismaUbicacionDebenSerIguales() {
        // Arrange & Act
        EncuentroSeguro a = new EncuentroSeguro("Biblioteca Central");
        EncuentroSeguro b = new EncuentroSeguro("Biblioteca Central");

        // Assert
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void noDebePermitirUnaUbicacionQueNoEsUnaZonaDelCampus() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> new EncuentroSeguro("Centro Comercial Portal"));
    }
}
