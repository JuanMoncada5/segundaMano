package com.uniquindio.segundaMano.infrastructure.rest.mapper;

import com.uniquindio.segundaMano.application.dto.RegistrarUniCambistaRequest;
import com.uniquindio.segundaMano.infrastructure.rest.dto.RegistrarUniCambistaBody;
import org.springframework.stereotype.Component;

@Component
public class UniCambistaRestMapper {

    public RegistrarUniCambistaRequest toRequest(RegistrarUniCambistaBody body) {
        return new RegistrarUniCambistaRequest(body.correoInstitucional(), body.nombre());
    }
}
