package com.uniquindio.segundaMano.support;

import com.uniquindio.segundaMano.application.auth.EmisorToken;
import com.uniquindio.segundaMano.application.auth.Rol;
import com.uniquindio.segundaMano.application.auth.TokenEmitido;

import java.time.Instant;

/** Token de mentira: sirve para comprobar que el caso de uso lo pide con el correo y el rol correctos. */
public class EmisorTokenFalso implements EmisorToken {

    @Override
    public TokenEmitido emitir(String correoInstitucional, Rol rol) {
        return new TokenEmitido("token-de-prueba|" + correoInstitucional + "|" + rol,
                Instant.parse("2030-01-01T00:00:00Z"));
    }
}
