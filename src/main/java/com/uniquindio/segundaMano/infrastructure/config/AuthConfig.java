package com.uniquindio.segundaMano.infrastructure.config;

import com.uniquindio.segundaMano.application.auth.AutenticarUseCase;
import com.uniquindio.segundaMano.application.auth.CodificadorContrasena;
import com.uniquindio.segundaMano.application.auth.CredencialRepository;
import com.uniquindio.segundaMano.application.auth.EmisorToken;
import com.uniquindio.segundaMano.application.auth.RegistrarCuentaUseCase;
import com.uniquindio.segundaMano.application.usecase.RegistrarUniCambistaUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Beans de autenticación (aparte de UseCaseConfig para que dos personas no editen el mismo archivo). */
@Configuration
public class AuthConfig {

    @Bean
    public RegistrarCuentaUseCase registrarCuentaUseCase(RegistrarUniCambistaUseCase registrarUniCambista,
                                                         CredencialRepository credenciales,
                                                         CodificadorContrasena codificador) {
        return new RegistrarCuentaUseCase(registrarUniCambista, credenciales, codificador);
    }

    @Bean
    public AutenticarUseCase autenticarUseCase(CredencialRepository credenciales,
                                               CodificadorContrasena codificador,
                                               EmisorToken emisorToken) {
        return new AutenticarUseCase(credenciales, codificador, emisorToken);
    }
}
