package com.uniquindio.segundaMano.application.dto;

import com.uniquindio.segundaMano.domain.entity.Transaccion;
import com.uniquindio.segundaMano.domain.valueObject.EncuentroSeguro;

import java.math.BigDecimal;

/**
 * zonaEncuentro y referenciaEncuentro pueden ser null mientras no se haya acordado el EncuentroSeguro.
 */
public record TransaccionResponse(
        String id,
        String codigoPublicacion,
        String correoComprador,
        String correoVendedor,
        BigDecimal montoAcordado,
        String estado,
        String zonaEncuentro,
        String referenciaEncuentro
) {
    public static TransaccionResponse desde(Transaccion transaccion) {
        EncuentroSeguro encuentro = transaccion.getEncuentroSeguro();
        return new TransaccionResponse(
                transaccion.getId(),
                transaccion.getPublicacion().getCodigo(),
                transaccion.getComprador().getCorreoInstitucional(),
                transaccion.getVendedor().getCorreoInstitucional(),
                transaccion.getMontoAcordado(),
                transaccion.getEstado().name(),
                encuentro != null ? encuentro.zona().name() : null,
                encuentro != null ? encuentro.referencia() : null
        );
    }
}