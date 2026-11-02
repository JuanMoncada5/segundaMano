package com.uniquindio.segundaMano.support;

import com.uniquindio.segundaMano.application.auth.CodificadorContrasena;

/** "Hash" de mentira (prefijo + texto) para probar los casos de uso sin depender de BCrypt. */
public class CodificadorFalso implements CodificadorContrasena {

    @Override
    public String codificar(String contrasenaPlana) {
        return "hash:" + contrasenaPlana;
    }

    @Override
    public boolean coincide(String contrasenaPlana, String contrasenaHash) {
        return codificar(contrasenaPlana).equals(contrasenaHash);
    }
}
