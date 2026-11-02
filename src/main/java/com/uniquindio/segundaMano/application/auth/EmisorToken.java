package com.uniquindio.segundaMano.application.auth;

/** Puerto: fabrica el token firmado. La implementación (JWT) vive en infrastructure. */
public interface EmisorToken {

    TokenEmitido emitir(String correoInstitucional, Rol rol);
}
