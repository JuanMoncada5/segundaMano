package com.uniquindio.segundaMano.infrastructure.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/** Cuerpo JSON que llega por HTTP. El oferente NO viaja aquí: sale del token JWT. */
public record OfertarArticuloBody(
        @NotNull(message = "montoPropuesto es obligatorio")
        @Positive(message = "montoPropuesto debe ser mayor que cero") BigDecimal montoPropuesto) {
}
