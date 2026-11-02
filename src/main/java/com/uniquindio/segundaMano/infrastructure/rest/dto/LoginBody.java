package com.uniquindio.segundaMano.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginBody(
        @NotBlank(message = "correoInstitucional es obligatorio") String correoInstitucional,
        @NotBlank(message = "contrasena es obligatoria") String contrasena) {
}
