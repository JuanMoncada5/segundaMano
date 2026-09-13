package com.uniquindio.segundaMano.domain.valueobject;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;

public record EncuentroSeguro(String nombreUbicacion) {

    private static final java.util.List<String> ZONAS_VALIDAS = java.util.List.of(
            "Biblioteca", "Canchas", "Edificios", "Porterias", "Cafeterias"
    );

    public EncuentroSeguro {
        if (nombreUbicacion == null || nombreUbicacion.isBlank()) {
            throw new ReglaDominioException("El EncuentroSeguro debe tener una ubicación");
        }
        boolean esValida = ZONAS_VALIDAS.stream()
                .anyMatch(zona -> nombreUbicacion.toLowerCase().contains(zona.toLowerCase()));
        if (!esValida) {
            throw new ReglaDominioException("La ubicación debe corresponder a una zona del Campus");
        }
    }
}
