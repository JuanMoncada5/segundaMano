package com.uniquindio.segundaMano.domain.valueObject;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;

public record RachaConfianza(double promedioEstrellas, int totalTransacciones) {

    public RachaConfianza {
        if (promedioEstrellas < 0.0 || promedioEstrellas > 5.0) {
            throw new ReglaDominioException("El promedio de estrellas debe estar entre 0.0 y 5.0");
        }
        if (totalTransacciones < 0) {
            throw new ReglaDominioException("El total de transacciones no puede ser negativo");
        }
    }

    public static RachaConfianza inicial() {
        return new RachaConfianza(5.0, 0);
    }

    public boolean debeMostrarAdvertencia() {
        return totalTransacciones >= 5 && promedioEstrellas < 3.0;
    }
}
