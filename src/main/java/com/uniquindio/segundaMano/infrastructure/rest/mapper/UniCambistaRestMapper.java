package com.uniquindio.segundaMano.infrastructure.rest.mapper;

import com.uniquindio.segundaMano.application.auth.LoginRequest;
import com.uniquindio.segundaMano.application.auth.RegistrarCuentaRequest;
import com.uniquindio.segundaMano.infrastructure.rest.dto.LoginBody;
import com.uniquindio.segundaMano.infrastructure.rest.dto.RegistrarUniCambistaBody;
import org.springframework.stereotype.Component;

@Component
public class UniCambistaRestMapper {

    public RegistrarCuentaRequest toRequest(RegistrarUniCambistaBody body) {
        return new RegistrarCuentaRequest(body.correoInstitucional(), body.nombre(), body.contrasena());
    }

    public LoginRequest toRequest(LoginBody body) {
        return new LoginRequest(body.correoInstitucional(), body.contrasena());
    }
}
