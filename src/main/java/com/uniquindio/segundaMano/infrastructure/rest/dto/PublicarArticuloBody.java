package com.uniquindio.segundaMano.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/** Cuerpo JSON que llega por HTTP. El dueño NO viaja aquí: sale del token JWT. */
public record PublicarArticuloBody(
        @NotBlank(message = "titulo es obligatorio") String titulo,
        @NotNull(message = "valor es obligatorio")
        @Positive(message = "valor debe ser mayor que cero") BigDecimal valor,
        String curso,
        String semestre) {
}
