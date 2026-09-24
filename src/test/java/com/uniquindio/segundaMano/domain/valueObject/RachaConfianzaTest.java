package com.uniquindio.segundaMano.domain.valueObject;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RachaConfianzaTest {

    @Test
    void dosRachaConfianzaConElMismoValorDebenSerIguales() {
        // Arrange
        RachaConfianza r1 = new RachaConfianza(4.5, 10);
        RachaConfianza r2 = new RachaConfianza(4.5, 10);

        // Act & Assert
        assertEquals(r1, r2); // Value Object: igual por VALOR, no por identidad
    }

    @Test
    void noDebePermitirRachaConfianzaConPromedioFueraDeRango() {
        // Arrange & Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            new RachaConfianza(6.0, 3); // 6.0 supera el máximo de 5.0 estrellas
        });
    }
}