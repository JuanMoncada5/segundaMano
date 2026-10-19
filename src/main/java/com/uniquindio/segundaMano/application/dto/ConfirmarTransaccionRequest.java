package com.uniquindio.segundaMano.application.dto;

public record ConfirmarTransaccionRequest(
        String idTransaccion,
        String correoUniCambista
) {
}
