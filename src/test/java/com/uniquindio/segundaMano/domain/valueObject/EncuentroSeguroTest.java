package com.uniquindio.segundaMano.domain.valueObject;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncuentroSeguroTest {

    @Test
    void dosEncuentrosSegurosConLaMismaZonaYReferenciaDebenSerIguales() {
        // Arrange & Act
        EncuentroSeguro a = new EncuentroSeguro(ZonaCampus.BIBLIOTECA, "Central");
        EncuentroSeguro b = new EncuentroSeguro(ZonaCampus.BIBLIOTECA, "Central");

        // Assert
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void noDebePermitirUnEncuentroSeguroSinZonaDelCampus() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> new EncuentroSeguro(null, "Centro Comercial Portal"));
    }

    @Test
    void laReferenciaVaciaSeNormalizaANull() {
        // Arrange & Act
        EncuentroSeguro conEspacios = new EncuentroSeguro(ZonaCampus.CANCHAS, "   ");
        EncuentroSeguro sinReferencia = new EncuentroSeguro(ZonaCampus.CANCHAS);

        // Assert
        assertNull(conEspacios.referencia());
        assertEquals(sinReferencia, conEspacios);
    }
}
