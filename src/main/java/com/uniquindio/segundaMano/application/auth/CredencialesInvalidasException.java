package com.uniquindio.segundaMano.application.auth;

/** Correo o contraseña incorrectos. Se usa el mismo mensaje en ambos casos para no revelar qué correos existen. */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Correo o contraseña incorrectos");
    }
}
