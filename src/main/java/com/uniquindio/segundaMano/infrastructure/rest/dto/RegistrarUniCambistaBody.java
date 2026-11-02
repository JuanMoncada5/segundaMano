package com.uniquindio.segundaMano.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Cuerpo JSON que llega por HTTP. Lo que viaja en la URL (códigos, ids) no va aquí. */
public record RegistrarUniCambistaBody(
        @NotBlank(message = "correoInstitucional es obligatorio") String correoInstitucional,
        @NotBlank(message = "nombre es obligatorio") String nombre,
        @NotBlank(message = "contrasena es obligatoria")
        @Size(min = 8, max = 72, message = "contrasena debe tener entre 8 y 72 caracteres") String contrasena) {
}
