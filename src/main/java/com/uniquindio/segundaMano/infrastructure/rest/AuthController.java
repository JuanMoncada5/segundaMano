package com.uniquindio.segundaMano.infrastructure.rest;

import com.uniquindio.segundaMano.application.auth.AutenticarUseCase;
import com.uniquindio.segundaMano.application.auth.LoginResponse;
import com.uniquindio.segundaMano.infrastructure.rest.dto.LoginBody;
import com.uniquindio.segundaMano.infrastructure.rest.mapper.UniCambistaRestMapper;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AutenticarUseCase autenticar;
    private final UniCambistaRestMapper mapper;

    public AuthController(AutenticarUseCase autenticar, UniCambistaRestMapper mapper) {
        this.autenticar = autenticar;
        this.mapper = mapper;
    }

    /** Ruta pública: devuelve el token que luego se envía como "Authorization: Bearer ...". */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginBody body) {
        return autenticar.ejecutar(mapper.toRequest(body));
    }
}
