package com.uniquindio.segundaMano.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/** Cuerpo JSON que llega por HTTP. Lo que viaja en la URL (códigos, ids) no va aquí. */
public record PublicarArticuloBody(
        @NotBlank(message = "correoDueno es obligatorio") String correoDueno,
        @NotBlank(message = "titulo es obligatorio") String titulo,
        @NotNull(message = "valor es obligatorio")
        @Positive(message = "valor debe ser mayor que cero") BigDecimal valor,
        String curso,
        String semestre) {
}
