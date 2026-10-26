package com.uniquindio.segundaMano.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;

/** Cuerpo JSON que llega por HTTP. Lo que viaja en la URL (códigos, ids) no va aquí. */
public record EncuentroSeguroBody(
        @NotBlank(message = "correoUniCambista es obligatorio") String correoUniCambista,
        @NotBlank(message = "zona es obligatoria") String zona,
        @NotBlank(message = "referencia es obligatoria") String referencia) {
}
