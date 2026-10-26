package com.uniquindio.segundaMano.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/** Cuerpo JSON que llega por HTTP. Lo que viaja en la URL (códigos, ids) no va aquí. */
public record OfertarArticuloBody(
        @NotBlank(message = "correoOferente es obligatorio") String correoOferente,
        @NotNull(message = "montoPropuesto es obligatorio")
        @Positive(message = "montoPropuesto debe ser mayor que cero") BigDecimal montoPropuesto) {
}
