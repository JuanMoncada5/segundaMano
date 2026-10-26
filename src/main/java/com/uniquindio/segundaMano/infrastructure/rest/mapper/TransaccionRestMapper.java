package com.uniquindio.segundaMano.infrastructure.rest.mapper;

import com.uniquindio.segundaMano.application.dto.AsignarEncuentroSeguroRequest;
import com.uniquindio.segundaMano.application.dto.ConfirmarTransaccionRequest;
import com.uniquindio.segundaMano.infrastructure.rest.dto.ConfirmacionBody;
import com.uniquindio.segundaMano.infrastructure.rest.dto.EncuentroSeguroBody;
import org.springframework.stereotype.Component;

@Component
public class TransaccionRestMapper {

    public AsignarEncuentroSeguroRequest toRequest(String idTransaccion, EncuentroSeguroBody body) {
        return new AsignarEncuentroSeguroRequest(
                idTransaccion, body.correoUniCambista(), body.zona(), body.referencia());
    }

    public ConfirmarTransaccionRequest toRequest(String idTransaccion, ConfirmacionBody body) {
        return new ConfirmarTransaccionRequest(idTransaccion, body.correoUniCambista());
    }
}