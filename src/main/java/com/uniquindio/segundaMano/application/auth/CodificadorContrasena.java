package com.uniquindio.segundaMano.application.auth;

/** Puerto: quien lo implemente decide el algoritmo (BCrypt en infrastructure). */
public interface CodificadorContrasena {

    String codificar(String contrasenaPlana);

    boolean coincide(String contrasenaPlana, String contrasenaHash);
}
