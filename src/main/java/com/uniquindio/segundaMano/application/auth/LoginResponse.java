package com.uniquindio.segundaMano.application.auth;

public record LoginResponse(String token, String tipo, String expiraEn, String correoInstitucional, String rol) {
}
