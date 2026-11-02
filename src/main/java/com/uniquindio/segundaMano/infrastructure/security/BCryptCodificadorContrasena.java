package com.uniquindio.segundaMano.infrastructure.security;

import com.uniquindio.segundaMano.application.auth.CodificadorContrasena;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BCrypt: función de hash pensada para contraseñas. Es lenta a propósito y le agrega una "sal" aleatoria,
 * por eso dos usuarios con la misma contraseña tienen hashes distintos y no se puede revertir.
 */
@Component
public class BCryptCodificadorContrasena implements CodificadorContrasena {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String codificar(String contrasenaPlana) {
        return encoder.encode(contrasenaPlana);
    }

    @Override
    public boolean coincide(String contrasenaPlana, String contrasenaHash) {
        return encoder.matches(contrasenaPlana, contrasenaHash);
    }
}
