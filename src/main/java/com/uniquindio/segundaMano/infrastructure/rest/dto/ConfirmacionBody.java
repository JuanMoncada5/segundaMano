package com.uniquindio.segundaMano.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;

/** Cuerpo JSON que llega por HTTP. Lo que viaja en la URL (códigos, ids) no va aquí. */
public record ConfirmacionBody(@NotBlank(message = "correoUniCambista es obligatorio") String correoUniCambista) {
}
