package com.uniquindio.segundaMano.application.dto;

import java.math.BigDecimal;

public record OfertarArticuloRequest(String codigoPublicacion, String correoOferente, BigDecimal montoPropuesto) {
}