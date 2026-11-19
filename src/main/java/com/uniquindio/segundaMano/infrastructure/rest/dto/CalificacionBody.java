package com.uniquindio.segundaMano.infrastructure.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Cuerpo JSON para calificar. Quién califica sale del token; la Transacción viaja en la URL. */
public record CalificacionBody(
        @NotNull(message = "estrellas es obligatorio")
        @Min(value = 1, message = "estrellas debe ser mínimo 1")
        @Max(value = 5, message = "estrellas debe ser máximo 5")
        Integer estrellas) {
}