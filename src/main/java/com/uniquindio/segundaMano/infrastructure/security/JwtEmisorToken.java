package com.uniquindio.segundaMano.infrastructure.security;

import com.uniquindio.segundaMano.application.auth.EmisorToken;
import com.uniquindio.segundaMano.application.auth.Rol;
import com.uniquindio.segundaMano.application.auth.TokenEmitido;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Fabrica el JWT: header (algoritmo HS256) + payload (correo, rol, emisión, vencimiento) + firma.
 * La firma usa la clave secreta de application.properties; el payload NO está cifrado (cualquiera lo puede leer),
 * pero si alguien lo modifica la firma deja de coincidir y el token se rechaza. Por eso nunca lleva la contraseña.
 */
@Component
public class JwtEmisorToken implements EmisorToken {

    private final JwtEncoder encoder;
    private final Duration duracion;

    public JwtEmisorToken(JwtEncoder encoder, @Value("${app.jwt.expiracion-minutos}") long minutos) {
        this.encoder = encoder;
        this.duracion = Duration.ofMinutes(minutos);
    }

    @Override
    public TokenEmitido emitir(String correoInstitucional, Rol rol) {
        Instant ahora = Instant.now();
        Instant vence = ahora.plus(duracion);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("unisegundamano")
                .subject(correoInstitucional)
                .issuedAt(ahora)
                .expiresAt(vence)
                .claim("roles", List.of(rol.name()))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenEmitido(token, vence);
    }
}
