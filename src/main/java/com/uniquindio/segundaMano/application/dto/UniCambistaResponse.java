package com.uniquindio.segundaMano.application.dto;

import com.uniquindio.segundaMano.domain.entity.UniCambista;

public record UniCambistaResponse(
        String correoInstitucional,
        String nombre,
        boolean verificado,
        double promedioEstrellas,
        int totalTransacciones
) {
    public static UniCambistaResponse desde(UniCambista uniCambista) {
        return new UniCambistaResponse(
                uniCambista.getCorreoInstitucional(),
                uniCambista.getNombre(),
                uniCambista.isVerificado(),
                uniCambista.getRachaConfianza().promedioEstrellas(),
                uniCambista.getRachaConfianza().totalTransacciones()
        );
    }
}
