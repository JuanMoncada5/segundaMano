package com.uniquindio.segundaMano.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;

/** Cuerpo JSON que llega por HTTP. Lo que viaja en la URL (códigos, ids) no va aquí. */
public record RegistrarUniCambistaBody(
        @NotBlank(message = "correoInstitucional es obligatorio") String correoInstitucional,
        @NotBlank(message = "nombre es obligatorio") String nombre) {
}
