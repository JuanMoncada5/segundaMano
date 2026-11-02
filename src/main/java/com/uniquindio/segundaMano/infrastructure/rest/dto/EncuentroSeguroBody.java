package com.uniquindio.segundaMano.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;

/** Cuerpo JSON que llega por HTTP. Quien asigna el encuentro NO viaja aquí: sale del token JWT. */
public record EncuentroSeguroBody(
        @NotBlank(message = "zona es obligatoria") String zona,
        @NotBlank(message = "referencia es obligatoria") String referencia) {
}
