package com.uniquindio.segundaMano.application.auth;

/** Datos para iniciar sesión. La contraseña se guarda siempre como hash (BCrypt), nunca en texto plano. */
public record Credencial(String correoInstitucional, String contrasenaHash, Rol rol) {
}
