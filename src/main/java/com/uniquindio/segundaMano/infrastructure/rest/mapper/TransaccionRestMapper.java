package com.uniquindio.segundaMano.infrastructure.rest.mapper;

import com.uniquindio.segundaMano.application.dto.AsignarEncuentroSeguroRequest;
import com.uniquindio.segundaMano.application.dto.ConfirmarTransaccionRequest;
import com.uniquindio.segundaMano.infrastructure.rest.dto.EncuentroSeguroBody;
import org.springframework.stereotype.Component;

@Component
public class TransaccionRestMapper {

    /** El correo de quien asigna el encuentro viene del token (jwt.sub), nunca del cuerpo. */
    public AsignarEncuentroSeguroRequest toRequest(String idTransaccion, String correoUniCambista, EncuentroSeguroBody body) {
        return new AsignarEncuentroSeguroRequest(
                idTransaccion, correoUniCambista, body.zona(), body.referencia());
    }

    /** Confirmar no necesita cuerpo: quien confirma viene del token (jwt.sub). */
    public ConfirmarTransaccionRequest toRequest(String idTransaccion, String correoUniCambista) {
        return new ConfirmarTransaccionRequest(idTransaccion, correoUniCambista);
    }
}